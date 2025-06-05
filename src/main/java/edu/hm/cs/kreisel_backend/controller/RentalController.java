package edu.hm.cs.kreisel_backend.controller;

import edu.hm.cs.kreisel_backend.model.Rental;
import edu.hm.cs.kreisel_backend.model.User;
import edu.hm.cs.kreisel_backend.service.RentalService;
import edu.hm.cs.kreisel_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;
    private final UserService userService;


    // Admin: Alle Rentals abrufen
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Rental>> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    // Admin: Alle überfälligen Rentals abrufen
    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Rental>> getOverdueRentals() {
        return ResponseEntity.ok(rentalService.getOverdueRentals());
    }

    // Admin: Rentals eines Users abrufen, User: eigene Rentals abrufen
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Rental>> getRentalsByUser(@PathVariable Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.getUserByEmail(email);

        // Prüfen ob User seine eigenen Rentals abruft oder Admin ist
        if (currentUser.getId().equals(userId) || currentUser.getRole() == User.Role.ADMIN) {
            return ResponseEntity.ok(rentalService.getRentalsByUser(userId));
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    // Admin: Aktive Rentals eines Users abrufen, User: eigene aktive Rentals abrufen
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<Rental>> getActiveRentalsByUser(@PathVariable Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.getUserByEmail(email);

        // Prüfen ob User seine eigenen aktiven Rentals abruft oder Admin ist
        if (currentUser.getId().equals(userId) || currentUser.getRole() == User.Role.ADMIN) {
            return ResponseEntity.ok(rentalService.getActiveRentalsByUser(userId));
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    // Admin: Historische Rentals eines Users abrufen, User: eigene historische Rentals abrufen
    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<Rental>> getHistoricalRentalsByUser(@PathVariable Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.getUserByEmail(email);

        // Prüfen ob User seine eigenen historischen Rentals abruft oder Admin ist
        if (currentUser.getId().equals(userId) || currentUser.getRole() == User.Role.ADMIN) {
            return ResponseEntity.ok(rentalService.getHistoricalRentalsByUser(userId));
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    // User: Item ausleihen (nur für sich selbst)
    @PostMapping("/user/{userId}/rent")
    public ResponseEntity<Rental> rentItem(@PathVariable Long userId, @Valid @RequestBody Map<String, String> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.getUserByEmail(email);

        // Prüfen ob User für sich selbst ausleiht oder Admin ist
        if (currentUser.getId().equals(userId) || currentUser.getRole() == User.Role.ADMIN) {
            Long itemId = Long.valueOf(request.get("itemId"));
            LocalDate endDate = LocalDate.parse(request.get("endDate"));
            return ResponseEntity.ok(rentalService.rentItem(userId, itemId, endDate));
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    // User: Ausleihe verlängern (nur eigene), Admin: jede Ausleihe verlängern
    @PostMapping("/{rentalId}/extend")
    public ResponseEntity<Rental> extendRental(@PathVariable Long rentalId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.getUserByEmail(email);

        // Rental abrufen
        Rental rental = rentalService.getActiveRentalForItem(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental nicht gefunden"));

        // Prüfen ob User seine eigene Ausleihe verlängert oder Admin ist
        if (rental.getUser().getId().equals(currentUser.getId()) || currentUser.getRole() == User.Role.ADMIN) {
            return ResponseEntity.ok(rentalService.extendRental(rentalId));
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    // User: Item zurückgeben (nur eigene), Admin: jedes Item zurückgeben
    @PostMapping("/{rentalId}/return")
    public ResponseEntity<Rental> returnRental(@PathVariable Long rentalId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.getUserByEmail(email);

        // Rental abrufen
        Rental rental = rentalService.getActiveRentalForItem(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental nicht gefunden"));

        // Prüfen ob User seine eigene Ausleihe zurückgibt oder Admin ist
        if (rental.getUser().getId().equals(currentUser.getId()) || currentUser.getRole() == User.Role.ADMIN) {
            return ResponseEntity.ok(rentalService.returnRental(rentalId));
        } else {
            return ResponseEntity.status(403).build();
        }
    }
}
