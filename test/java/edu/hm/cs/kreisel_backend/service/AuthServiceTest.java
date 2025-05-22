package edu.hm.cs.kreisel_backend.service;

import edu.hm.cs.kreisel_backend.dto.AuthRequestDto;
import edu.hm.cs.kreisel_backend.model.User;
import edu.hm.cs.kreisel_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private AuthRequestDto validRequest;
    private AuthRequestDto invalidEmailRequest;
    private AuthRequestDto shortPasswordRequest;
    private User existingUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        validRequest = new AuthRequestDto();
        validRequest.setEmail("user@hm.edu");
        validRequest.setPassword("password123");

        invalidEmailRequest = new AuthRequestDto();
        invalidEmailRequest.setEmail("user@gmail.com");
        invalidEmailRequest.setPassword("password123");

        shortPasswordRequest = new AuthRequestDto();
        shortPasswordRequest.setEmail("user@hm.edu");
        shortPasswordRequest.setPassword("pass");

        existingUser = new User();
        existingUser.setEmail("existing@hm.edu");
        existingUser.setPassword("encoded_password");
        existingUser.setRole(User.Role.USER);

        // Mock the password encoder
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
        when(passwordEncoder.matches("wrong_password", "encoded_password")).thenReturn(false);

        // Mock the repository for existing user
        when(userRepository.findByEmail("existing@hm.edu")).thenReturn(existingUser);
        when(userRepository.findByEmail("admin@hm.edu")).thenReturn(null);
        when(userRepository.findByEmail("user@hm.edu")).thenReturn(null);

        // Mock the save method
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            // simulate ID assignment
            return savedUser;
        });
    }

    @Test
    void registerUser_WithValidHmEmail_ShouldCreateUser() {
        // Arrange
        // Act
        User result = authService.registerUser(validRequest);

        // Assert
        assertNotNull(result);
        assertEquals("user@hm.edu", result.getEmail());
        assertEquals("encoded_password", result.getPassword());
        assertEquals(User.Role.USER, result.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_WithAdminHmEmail_ShouldCreateAdminUser() {
        // Arrange
        AuthRequestDto adminRequest = new AuthRequestDto();
        adminRequest.setEmail("admin@hm.edu");
        adminRequest.setPassword("password123");

        // Act
        User result = authService.registerUser(adminRequest);

        // Assert
        assertNotNull(result);
        assertEquals("admin@hm.edu", result.getEmail());
        assertEquals(User.Role.ADMIN, result.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_WithInvalidEmail_ShouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.registerUser(invalidEmailRequest)
        );
        assertEquals("Nur HM-E-Mail-Adressen sind erlaubt", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithShortPassword_ShouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.registerUser(shortPasswordRequest)
        );
        assertEquals("Passwort muss mindestens 6 Zeichen lang sein", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithExistingEmail_ShouldThrowException() {
        // Arrange
        AuthRequestDto existingEmailRequest = new AuthRequestDto();
        existingEmailRequest.setEmail("existing@hm.edu");
        existingEmailRequest.setPassword("password123");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.registerUser(existingEmailRequest)
        );
        assertEquals("Benutzer mit dieser E-Mail existiert bereits", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loadUserByUsername_WithExistingEmail_ShouldReturnUserDetails() {
        // Act
        org.springframework.security.core.userdetails.User result =
                (org.springframework.security.core.userdetails.User) authService.loadUserByUsername("existing@hm.edu");

        // Assert
        assertNotNull(result);
        assertEquals("existing@hm.edu", result.getUsername());
        assertEquals("encoded_password", result.getPassword());
        assertTrue(result.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_WithNonExistingEmail_ShouldThrowException() {
        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authService.loadUserByUsername("nonexisting@hm.edu")
        );
        assertEquals("Benutzer nicht gefunden mit E-Mail: nonexisting@hm.edu", exception.getMessage());
    }

    @Test
    void checkPassword_WithCorrectPassword_ShouldReturnTrue() {
        // Act
        boolean result = authService.checkPassword("password123", "encoded_password");

        // Assert
        assertTrue(result);
    }

    @Test
    void checkPassword_WithWrongPassword_ShouldReturnFalse() {
        // Act
        boolean result = authService.checkPassword("wrong_password", "encoded_password");

        // Assert
        assertFalse(result);
    }
}