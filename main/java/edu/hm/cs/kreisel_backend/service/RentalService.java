
// RentalService.java
package edu.hm.cs.kreisel_backend.service;

import edu.hm.cs.kreisel_backend.dto.CreateRentalDto;
import edu.hm.cs.kreisel_backend.dto.RentalDto;
import edu.hm.cs.kreisel_backend.model.Item;
import edu.hm.cs.kreisel_backend.model.Rental;
import edu.hm.cs.kreisel_backend.model.User;
import edu.hm.cs.kreisel_backend.repository.ItemRepository;
import edu.hm.cs.kreisel_backend.repository.RentalRepository;
import edu.hm.cs.kreisel_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    public List<RentalDto> getRentalsByUser(UUID userId) {
        return rentalRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<RentalDto> getRentalsByItem(UUID itemId) {
        return rentalRepository.findByItemId(itemId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public RentalDto createRental(CreateRentalDto createRentalDto) {
        // Validate user exists
        User user = userRepository.findById(createRentalDto.userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate item exists and is available
        Item item = itemRepository.findById(createRentalDto.itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.isAvailable()) {
            throw new RuntimeException("Item is not available for rental");
        }

        // Check if user can rent more items
        if (!user.canRent()) {
            throw new RuntimeException("User has reached maximum active rentals (5)");
        }

        // Validate rental duration (max 4 months)
        if (ChronoUnit.DAYS.between(createRentalDto.startDate, createRentalDto.endDate) > 120) {
            throw new RuntimeException("Rental period cannot exceed 4 months");
        }

        // Create rental
        Rental rental = new Rental();
        rental.setUser(user);
        rental.setItem(item);
        rental.setStartDate(createRentalDto.startDate);
        rental.setEndDate(createRentalDto.endDate);
        rental.setReturned(false);

        // Update item status
        item.setStatus(Item.Status.NichtVerfugbar);
        itemRepository.save(item);

        // Save rental
        Rental savedRental = rentalRepository.save(rental);
        return convertToDto(savedRental);
    }

    public RentalDto returnItem(UUID rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));

        if (rental.isReturned()) {
            throw new RuntimeException("Item already returned");
        }

        // Mark as returned
        rental.setReturned(true);
        rental.setReturnedAt(LocalDate.now());

        // Update item status
        Item item = rental.getItem();
        item.setStatus(Item.Status.Verfugbar);
        itemRepository.save(item);

        // Save rental
        Rental updatedRental = rentalRepository.save(rental);
        return convertToDto(updatedRental);
    }

    private RentalDto convertToDto(Rental rental) {
        RentalDto dto = new RentalDto();
        dto.id = rental.getId();
        dto.userId = rental.getUser().getId();
        dto.itemId = rental.getItem().getId();
        dto.startDate = rental.getStartDate();
        dto.endDate = rental.getEndDate();
        dto.returned = rental.isReturned();
        dto.returnedAt = rental.getReturnedAt();
        return dto;
    }
}

