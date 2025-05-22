package edu.hm.cs.kreisel_backend.service;

import edu.hm.cs.kreisel_backend.dto.CreateRentalDto;
import edu.hm.cs.kreisel_backend.dto.RentalDto;
import edu.hm.cs.kreisel_backend.model.Item;
import edu.hm.cs.kreisel_backend.model.Rental;
import edu.hm.cs.kreisel_backend.model.User;
import edu.hm.cs.kreisel_backend.repository.RentalRepository;
import edu.hm.cs.kreisel_backend.repository.UserRepository;
import edu.hm.cs.kreisel_backend.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private RentalService rentalService;

    private UUID userId;
    private UUID itemId;
    private User user;
    private Item item;
    private Rental rental;
    private Rental activeRental;
    private Rental returnedRental;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Setup common test data
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);

        itemId = UUID.randomUUID();
        item = new Item();
        item.setId(itemId);
        item.setStatus(Item.Status.Verfugbar);

        // Active rental
        activeRental = new Rental();
        activeRental.setId(UUID.randomUUID());
        activeRental.setUser(user);
        activeRental.setItem(item);
        activeRental.setStartDate(LocalDate.now());
        activeRental.setEndDate(LocalDate.now().plusDays(5));
        activeRental.setReturned(false);

        // Returned rental
        returnedRental = new Rental();
        returnedRental.setId(UUID.randomUUID());
        returnedRental.setUser(user);
        returnedRental.setItem(item);
        returnedRental.setStartDate(LocalDate.now().minusDays(10));
        returnedRental.setEndDate(LocalDate.now().minusDays(5));
        returnedRental.setReturned(true);
        returnedRental.setReturnedAt(LocalDate.now().minusDays(5));

        // Default rental for createRental test
        rental = activeRental;
    }

    @Test
    void testCreateRental() {
        // Arrange: Vorbereitung von Testdaten
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        UUID itemId = UUID.randomUUID();
        Item item = new Item();
        item.setId(itemId);
        item.setStatus(Item.Status.Verfugbar); // Item ist verfügbar

        Rental rental = new Rental();
        rental.setId(UUID.randomUUID());
        rental.setUser(user);
        rental.setItem(item);
        rental.setStartDate(LocalDate.now());
        rental.setEndDate(LocalDate.now().plusDays(5));
        rental.setReturned(false);

        CreateRentalDto dto = new CreateRentalDto();
        dto.setUserId(userId);
        dto.setItemId(itemId);
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusDays(5));

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(java.util.Optional.of(item));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);

        // Act: Ausführung der Methode
        RentalDto rentalDto = rentalService.createRental(dto);

        // Assert: Überprüfung des Ergebnisses
        assertNotNull(rentalDto, "RentalDto sollte nicht null sein");
        assertEquals(rental.getId(), rentalDto.getId(), "Die Rental ID sollte übereinstimmen");
        assertEquals(user.getId(), rentalDto.getUserId(), "Die User ID sollte übereinstimmen");
        assertEquals(item.getId(), rentalDto.getItemId(), "Die Item ID sollte übereinstimmen");
        assertEquals(rental.getStartDate(), rentalDto.getStartDate(), "Das Startdatum sollte übereinstimmen");
        assertEquals(rental.getEndDate(), rentalDto.getEndDate(), "Das Enddatum sollte übereinstimmen");

        // Verifizierung der Interaktionen
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }
    @Test
    void testGetAllRentals() {
        // Arrange
        List<Rental> rentals = Arrays.asList(activeRental, returnedRental);
        when(rentalRepository.findAll()).thenReturn(rentals);

        // Act
        List<RentalDto> result = rentalService.getAllRentals();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(activeRental.getId(), result.get(0).getId());
        assertEquals(returnedRental.getId(), result.get(1).getId());
        verify(rentalRepository, times(1)).findAll();
    }

    @Test
    void testGetAllActiveRentals() {
        // Arrange
        List<Rental> activeRentals = Collections.singletonList(activeRental);
        when(rentalRepository.findAllByReturnedFalse()).thenReturn(activeRentals);

        // Act
        List<RentalDto> result = rentalService.getAllActiveRentals();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(activeRental.getId(), result.get(0).getId());
        assertFalse(result.get(0).isReturned());
        verify(rentalRepository, times(1)).findAllByReturnedFalse();
    }

    @Test
    void testGetRentalsByUser() {
        // Arrange
        List<Rental> userRentals = Arrays.asList(activeRental, returnedRental);
        when(rentalRepository.findByUserId(userId)).thenReturn(userRentals);

        // Act
        List<RentalDto> result = rentalService.getRentalsByUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(activeRental.getId(), result.get(0).getId());
        assertEquals(returnedRental.getId(), result.get(1).getId());
        verify(rentalRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testGetActiveRentalsByUser() {
        // Arrange
        List<Rental> activeUserRentals = Collections.singletonList(activeRental);
        when(rentalRepository.findByUserIdAndReturnedFalse(userId)).thenReturn(activeUserRentals);

        // Act
        List<RentalDto> result = rentalService.getActiveRentalsByUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(activeRental.getId(), result.get(0).getId());
        assertFalse(result.get(0).isReturned());
        verify(rentalRepository, times(1)).findByUserIdAndReturnedFalse(userId);
    }

    @Test
    void testReturnItem() {
        // Arrange
        UUID rentalId = activeRental.getId();
        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(activeRental));
        when(rentalRepository.save(any(Rental.class))).thenReturn(activeRental);

        // Act
        RentalDto result = rentalService.returnItem(rentalId);

        // Assert
        assertNotNull(result);
        assertEquals(rentalId, result.getId());
        assertTrue(activeRental.isReturned());
        assertEquals(Item.Status.Verfugbar, activeRental.getItem().getStatus());
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(rentalRepository, times(1)).save(activeRental);
        verify(itemRepository, times(1)).save(activeRental.getItem());
    }

    @Test
    void testUpdateOverdueRentals() {
        // Arrange
        LocalDate today = LocalDate.now();
        Rental overdueRental = new Rental();
        overdueRental.setId(UUID.randomUUID());
        overdueRental.setUser(user);
        overdueRental.setItem(item);
        overdueRental.setStartDate(today.minusDays(10));
        overdueRental.setEndDate(today.minusDays(1));
        overdueRental.setReturned(false);

        List<Rental> overdueRentals = Collections.singletonList(overdueRental);
        when(rentalRepository.findAllByReturnedFalseAndEndDateBefore(today)).thenReturn(overdueRentals);

        // Act
        rentalService.updateOverdueRentals();

        // Assert
        assertTrue(overdueRental.isReturned());
        assertNotNull(overdueRental.getReturnedAt());
        assertEquals(Item.Status.Verfugbar, overdueRental.getItem().getStatus());
        verify(rentalRepository, times(1)).findAllByReturnedFalseAndEndDateBefore(today);
        verify(rentalRepository, times(1)).saveAll(overdueRentals);
        verify(itemRepository, times(1)).saveAll(anyList());
    }
}
