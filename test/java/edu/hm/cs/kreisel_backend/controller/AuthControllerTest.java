package edu.hm.cs.kreisel_backend.controller;

import edu.hm.cs.kreisel_backend.dto.AuthRequestDto;
import edu.hm.cs.kreisel_backend.service.AuthService;
import edu.hm.cs.kreisel_backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void login_ValidCredentials_ReturnsToken() throws Exception {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String token = "valid-jwt-token";

        when(authService.loadUserByUsername(email))
                .thenReturn(new org.springframework.security.core.userdetails.User(email, password, Collections.emptyList()));
        when(authService.checkPassword(password, password)).thenReturn(true);
        when(jwtUtil.generateToken(email)).thenReturn(token);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(token));

        verify(authService).loadUserByUsername(email);
        verify(authService).checkPassword(password, password);
        verify(jwtUtil).generateToken(email);
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        // Arrange
        String email = "test@example.com";
        String password = "wrongPassword";

        when(authService.loadUserByUsername(email))
                .thenReturn(new org.springframework.security.core.userdetails.User(email, "correctPassword", Collections.emptyList()));
        when(authService.checkPassword(password, "correctPassword")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isUnauthorized());

        verify(authService).loadUserByUsername(email);
        verify(authService).checkPassword(password, "correctPassword");
    }

    @Test
    void login_UserNotFound_ThrowsException() throws Exception {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "password123";

        when(authService.loadUserByUsername(email)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Ungültige Zugangsdaten"));

        verify(authService).loadUserByUsername(email);
        verify(authService, never()).checkPassword(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void register_ValidRequest_ReturnsSuccessMessage() throws Exception {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        AuthRequestDto requestDto = new AuthRequestDto(email, password);

        doReturn(null).when(authService).registerUser(requestDto);

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Registrierung erfolgreich"));

        verify(authService).registerUser(requestDto);
    }

    @Test
    void register_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Arrange
        String email = "invalidemail";
        String password = "short";
        AuthRequestDto requestDto = new AuthRequestDto(email, password);

        doThrow(new IllegalArgumentException("Ungültige Anfrage"))
                .when(authService).registerUser(requestDto);

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Ungültige Anfrage"));

        verify(authService).registerUser(requestDto);
    }
}