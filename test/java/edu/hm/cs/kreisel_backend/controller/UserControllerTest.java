package edu.hm.cs.kreisel_backend.controller;

import edu.hm.cs.kreisel_backend.dto.UserDto;
import edu.hm.cs.kreisel_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDto mockUserDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        mockUserDto = new UserDto();
        mockUserDto.id = UUID.randomUUID();
        mockUserDto.email = "testuser@hm.edu";
        mockUserDto.role = edu.hm.cs.kreisel_backend.model.User.Role.USER;
    }

    @Test
    void getUserById_ShouldReturnUserDetails() throws Exception {
        // Arrange
        when(userService.getUserById(mockUserDto.id)).thenReturn(mockUserDto);

        // Act & Assert
        mockMvc.perform(get("/api/users/" + mockUserDto.id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockUserDto.id.toString()))
                .andExpect(jsonPath("$.email").value("testuser@hm.edu"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(userService, times(1)).getUserById(mockUserDto.id);
    }

    @Test
    void getUserById_NotFound_ShouldReturn500() throws Exception {
        // Arrange
        UUID nonExistingUserId = UUID.randomUUID();
        when(userService.getUserById(nonExistingUserId))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(get("/api/users/" + nonExistingUserId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()); // 500 für RuntimeException

        verify(userService, times(1)).getUserById(nonExistingUserId);
    }
}