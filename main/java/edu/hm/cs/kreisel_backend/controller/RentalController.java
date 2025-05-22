package edu.hm.cs.kreisel_backend.controller;

import edu.hm.cs.kreisel_backend.dto.CreateRentalDto;
import edu.hm.cs.kreisel_backend.dto.RentalDto;
import edu.hm.cs.kreisel_backend.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rentals")
public class RentalController {

    @Autowired private RentalService rentalService;

    /* ---------- ADMIN ---------- */

    @GetMapping                           // alle Rentals
    @PreAuthorize("hasRole('ADMIN')")
    public List<RentalDto> getAllRentals() {
        return rentalService.getAllRentals();
    }

    @GetMapping("/active")               // nur aktive Rentals
    @PreAuthorize("hasRole('ADMIN')")
    public List<RentalDto> getAllActiveRentals() {
        return rentalService.getAllActiveRentals();
    }

    /* ---------- USER ---------- */

    // eigene Rentals (voll)
    @GetMapping("/my")
    public List<RentalDto> getMyRentals(Authentication auth) {
        UUID userId = UUID.fromString(auth.getName()); // sofern Name = UUID, sonst UserService holen
        return rentalService.getRentalsByUser(userId);
    }

    // eigene aktive Rentals
    @GetMapping("/my/active")
    public List<RentalDto> getMyActiveRentals(Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return rentalService.getActiveRentalsByUser(userId);
    }

    /* ---------- RENTAL CRUD ---------- */

    @PostMapping                          // neue Ausleihe
    public RentalDto createRental(@RequestBody CreateRentalDto dto) {
        return rentalService.createRental(dto);
    }

    @PostMapping("/{id}/return")          // Rückgabe
    public RentalDto returnItem(@PathVariable UUID id) {
        return rentalService.returnItem(id);
    }
}
