package edu.hm.cs.kreisel_backend.service;

import edu.hm.cs.kreisel_backend.dto.CreateUserDto;
import edu.hm.cs.kreisel_backend.dto.UserDto;
import edu.hm.cs.kreisel_backend.model.User;
import edu.hm.cs.kreisel_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Setup common test user
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("test@hm.edu");
        testUser.setRole(User.Role.USER);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setEmail("user1@hm.edu");
        user1.setRole(User.Role.USER);

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setEmail("user2@hm.edu");
        user2.setRole(User.Role.ADMIN);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Act
        List<UserDto> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(user1.getEmail(), result.get(0).email);
        assertEquals(user2.getEmail(), result.get(1).email);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllUsersEmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<UserDto> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        UserDto result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.id);
        assertEquals(testUser.getEmail(), result.email);
        assertEquals(testUser.getRole(), result.role);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserByIdNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(nonExistentId);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void testGetUserByEmail() {
        // Arrange
        String email = "test@hm.edu";
        when(userRepository.findByEmail(email)).thenReturn(testUser);

        // Act
        UserDto result = userService.getUserByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.id);
        assertEquals(testUser.getEmail(), result.email);
        assertEquals(testUser.getRole(), result.role);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testGetUserByEmailNotFound() {
        // Arrange
        String email = "nonexistent@hm.edu";
        when(userRepository.findByEmail(email)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserByEmail(email);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testCreateUser() {
        // Arrange
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.email = "newuser@hm.edu";
        createUserDto.role = User.Role.USER;

        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setEmail(createUserDto.email);
        newUser.setRole(createUserDto.role);

        when(userRepository.findByEmail(createUserDto.email)).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // Act
        UserDto result = userService.createUser(createUserDto);

        // Assert
        assertNotNull(result);
        assertEquals(newUser.getId(), result.id);
        assertEquals(newUser.getEmail(), result.email);
        assertEquals(newUser.getRole(), result.role);
        verify(userRepository, times(1)).findByEmail(createUserDto.email);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserWithInvalidEmail() {
        // Arrange
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.email = "invalid@gmail.com"; // Not an HM email
        createUserDto.role = User.Role.USER;

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(createUserDto);
        });

        assertEquals("Only HM email addresses are allowed", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(userRepository, never()).findByEmail(any(String.class));
    }

    @Test
    void testCreateUserWithNullEmail() {
        // Arrange
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.email = null;
        createUserDto.role = User.Role.USER;

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(createUserDto);
        });

        assertEquals("Only HM email addresses are allowed", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(userRepository, never()).findByEmail(any(String.class));
    }

    @Test
    void testCreateUserWithExistingEmail() {
        // Arrange
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.email = "existing@hm.edu";
        createUserDto.role = User.Role.USER;

        when(userRepository.findByEmail(createUserDto.email)).thenReturn(new User());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(createUserDto);
        });

        assertEquals("User with this email already exists", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(createUserDto.email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser() {
        // Arrange
        CreateUserDto updateUserDto = new CreateUserDto();
        updateUserDto.email = "updated@hm.edu";
        updateUserDto.role = User.Role.ADMIN;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail(updateUserDto.email)).thenReturn(null);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail(updateUserDto.email);
        updatedUser.setRole(updateUserDto.role);

        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        UserDto result = userService.updateUser(userId, updateUserDto);

        // Assert
        assertNotNull(result);
        assertEquals(updateUserDto.email, result.email);
        assertEquals(updateUserDto.role, result.role);
        verify(userRepository).findById(userId);
        verify(userRepository, times(1)).findByEmail(updateUserDto.email);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        CreateUserDto updateUserDto = new CreateUserDto();
        updateUserDto.email = "updated@hm.edu";
        updateUserDto.role = User.Role.ADMIN;

        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(nonExistentId, updateUserDto);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(nonExistentId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserWithInvalidEmail() {
        // Arrange
        CreateUserDto updateUserDto = new CreateUserDto();
        updateUserDto.email = "invalid@gmail.com"; // Not an HM email
        updateUserDto.role = User.Role.ADMIN;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(userId, updateUserDto);
        });

        assertEquals("Only HM email addresses are allowed", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
        verify(userRepository, never()).findByEmail(any(String.class));
    }

    @Test
    void testUpdateUserWithNullEmail() {
        // Arrange
        CreateUserDto updateUserDto = new CreateUserDto();
        updateUserDto.email = null;
        updateUserDto.role = User.Role.ADMIN;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(userId, updateUserDto);
        });

        assertEquals("Only HM email addresses are allowed", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
        verify(userRepository, never()).findByEmail(any(String.class));
    }

    @Test
    void testUpdateUserWithExistingEmail() {
        // Arrange
        UUID differentUserId = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(differentUserId);

        CreateUserDto updateUserDto = new CreateUserDto();
        updateUserDto.email = "existing@hm.edu";
        updateUserDto.role = User.Role.ADMIN;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail(updateUserDto.email)).thenReturn(existingUser);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(userId, updateUserDto);
        });

        assertEquals("User with this email already exists", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByEmail(updateUserDto.email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUserNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.existsById(nonExistentId)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(nonExistentId);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).existsById(nonExistentId);
        verify(userRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void testCanUserRent() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(testUser.canRent()).thenReturn(true);

        // Act
        boolean result = userService.canUserRent(userId);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testCanUserRentNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.canUserRent(nonExistentId);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(nonExistentId);
    }
}