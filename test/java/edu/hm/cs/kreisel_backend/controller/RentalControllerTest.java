package edu.hm.cs.kreisel_backend.controller;

import edu.hm.cs.kreisel_backend.dto.CreateRentalDto;
import edu.hm.cs.kreisel_backend.dto.RentalDto;
import edu.hm.cs.kreisel_backend.service.RentalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RentalControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RentalService rentalService;

    @InjectMocks
    private RentalController rentalController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(rentalController).build();
    }

    @Test
    void testGetAllRentals_ShouldReturnListOfRentals() throws Exception {
        // Arrange
        UUID rentalId = UUID.randomUUID();
        RentalDto rentalDto = new RentalDto();
        rentalDto.setId(rentalId);
        rentalDto.setStartDate(LocalDate.now());
        rentalDto.setEndDate(LocalDate.now().plusDays(5));
        rentalDto.setReturned(false);

        when(rentalService.getAllRentals()).thenReturn(List.of(rentalDto));

        // Act & Assert
        mockMvc.perform(get("/rentals")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(rentalId.toString()))
                .andExpect(jsonPath("$[0].returned").value(false));

        verify(rentalService).getAllRentals();
    }

    @Test
    void testGetAllActiveRentals_ShouldReturnListOfActiveRentals() throws Exception {
        // Arrange
        UUID rentalId = UUID.randomUUID();
        RentalDto rentalDto = new RentalDto();
        rentalDto.setId(rentalId);
        rentalDto.setReturned(false);

        when(rentalService.getAllActiveRentals()).thenReturn(List.of(rentalDto));

        // Act & Assert
        mockMvc.perform(get("/rentals/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(rentalId.toString()))
                .andExpect(jsonPath("$[0].returned").value(false));

        verify(rentalService).getAllActiveRentals();
    }

    @Test
    void testCreateRental_ShouldReturnCreatedRental() throws Exception {
        // Arrange
        UUID rentalId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        CreateRentalDto createRentalDto = new CreateRentalDto();
        createRentalDto.setUserId(userId);
        createRentalDto.setItemId(itemId);
        createRentalDto.setStartDate(LocalDate.now());
        createRentalDto.setEndDate(LocalDate.now().plusDays(10));

        RentalDto rentalDto = new RentalDto();
        rentalDto.setId(rentalId);
        rentalDto.setUserId(userId);
        rentalDto.setItemId(itemId);
        rentalDto.setStartDate(createRentalDto.getStartDate());
        rentalDto.setEndDate(createRentalDto.getEndDate());
        rentalDto.setReturned(false);

        when(rentalService.createRental(any(CreateRentalDto.class))).thenReturn(rentalDto);

        // Act & Assert
        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"userId\":\"" + userId + "\"," +
                                "\"itemId\":\"" + itemId + "\"," +
                                "\"startDate\":\"" + createRentalDto.getStartDate() + "\"," +
                                "\"endDate\":\"" + createRentalDto.getEndDate() + "\"" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rentalId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.itemId").value(itemId.toString()))
                .andExpect(jsonPath("$.returned").value(false));

        verify(rentalService).createRental(any(CreateRentalDto.class));
    }

    @Test
    void testReturnRental_ShouldReturnUpdatedRental() throws Exception {
        // Arrange
        UUID rentalId = UUID.randomUUID();
        RentalDto rentalDto = new RentalDto();
        rentalDto.setId(rentalId);
        rentalDto.setReturned(true);
        rentalDto.setReturnedAt(LocalDate.now());

        when(rentalService.returnItem(rentalId)).thenReturn(rentalDto);

        // Act & Assert
        mockMvc.perform(put("/rentals/{id}/return", rentalId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rentalId.toString()))
                .andExpect(jsonPath("$.returned").value(true));

        verify(rentalService).returnItem(rentalId);
    }

    @Test
    void testGetRentalsByUser_ShouldReturnListOfUserRentals() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID rentalId = UUID.randomUUID();
        RentalDto rentalDto = new RentalDto();
        rentalDto.setId(rentalId);
        rentalDto.setUserId(userId);
        rentalDto.setReturned(false);

        when(rentalService.getRentalsByUser(userId)).thenReturn(List.of(rentalDto));

        // Act & Assert
        mockMvc.perform(get("/rentals/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(rentalId.toString()))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));

        verify(rentalService).getRentalsByUser(userId);
    }

    @Test
    void testGetActiveRentalsByUser_ShouldReturnListOfActiveUserRentals() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID rentalId = UUID.randomUUID();
        RentalDto rentalDto = new RentalDto();
        rentalDto.setId(rentalId);
        rentalDto.setUserId(userId);
        rentalDto.setReturned(false);

        when(rentalService.getActiveRentalsByUser(userId)).thenReturn(List.of(rentalDto));

        // Act & Assert
        mockMvc.perform(get("/rentals/user/{userId}/active", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(rentalId.toString()))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()))
                .andExpect(jsonPath("$[0].returned").value(false));

        verify(rentalService).getActiveRentalsByUser(userId);
    }
}