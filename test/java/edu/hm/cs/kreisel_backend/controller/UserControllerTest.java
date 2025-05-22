package edu.hm.cs.kreisel_backend.controller;

import edu.hm.cs.kreisel_backend.dto.UserDto;
import edu.hm.cs.kreisel_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService; // Service wird per Mockito gemockt

    @InjectMocks
    private UserController userController; // Controller mit gemockten Abhängigkeiten

    private UserDto mockUserDto;

    @BeforeEach
    void setUp() {
        // Initialisiere Mocks und stelle sie im Controller bereit
        MockitoAnnotations.openMocks(this);

        // MockMvc mit dem Controller initialisieren
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        // Beispiel-Testdaten
        mockUserDto = new UserDto();
        mockUserDto.id = UUID.randomUUID();
        mockUserDto.email = "testuser@hm.edu";
        mockUserDto.role = edu.hm.cs.kreisel_backend.model.User.Role.USER;
    }

    @Test
    @WithMockUser(username = "testuser@hm.edu", roles = "USER")
    void getUserById_ShouldReturnUserDetails() throws Exception {
        // Arrange: Service verhalten simulieren
        when(userService.getUserById(mockUserDto.id)).thenReturn(mockUserDto);

        // Act & Assert: Endpunkt erfolgreich aufrufen und validieren
        mockMvc.perform(get("/api/users/" + mockUserDto.id) // ACHTUNG: Pfad korrigiert
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Überprüfe HTTP 200 OK
                .andExpect(jsonPath("$.id").value(mockUserDto.id.toString()))
                .andExpect(jsonPath("$.email").value("testuser@hm.edu"))
                .andExpect(jsonPath("$.role").value("USER"));

        // Verifiziere die Interaktion mit dem Service
        verify(userService, times(1)).getUserById(mockUserDto.id);
    }

    @Test
    void getUserById_NotAuthorized_ShouldReturn403() throws Exception {
        // Act & Assert: Ohne Authentifizierung sollte ein 403-Fehler zurückgegeben werden
        mockMvc.perform(get("/api/users/" + mockUserDto.id) // ACHTUNG: Pfad korrigiert
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()); // HTTP 403: Zugriff verweigert

        // Service-Methoden sollten NICHT aufgerufen worden sein
        verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser(username = "admin@hm.edu", roles = "ADMIN")
    void getNonExistingUser_ShouldReturn404() throws Exception {
        // Arrange: Simuliere, dass der gesuchte User nicht existiert
        UUID nonExistingUserId = UUID.randomUUID();
        when(userService.getUserById(nonExistingUserId)).thenThrow(new RuntimeException("User not found"));

        // Act & Assert: Fehlerfall testen
        mockMvc.perform(get("/api/users/" + nonExistingUserId) // ACHTUNG: Pfad korrigiert
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // HTTP 404: Nicht gefunden
                .andExpect(content().string("User not found")); // Überprüfe Fehlernachricht

        // Verifiziere die Interaktion mit dem Service
        verify(userService, times(1)).getUserById(nonExistingUserId);
    }
}