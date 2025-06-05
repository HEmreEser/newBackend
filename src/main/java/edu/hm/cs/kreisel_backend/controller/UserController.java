package edu.hm.cs.kreisel_backend.controller;

import edu.hm.cs.kreisel_backend.model.Rental;
import edu.hm.cs.kreisel_backend.model.User;
import edu.hm.cs.kreisel_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Admin: Alle User abrufen
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Admin: User nach ID abrufen, User: eigenes Profil abrufen
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.getUserByEmail(email);

        // Prüfen ob User sein eigenes Profil abruft oder Admin ist
        if (currentUser.getId().equals(id) || currentUser.getRole() == User.Role.ADMIN) {
            return ResponseEntity.ok(userService.getUserById(id));
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    // Admin: User nach Email suchen
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    // Admin: Rentals eines Users abrufen, User: eigene Rentals abrufen
    @GetMapping("/{id}/rentals")
    public ResponseEntity<List<Rental>> getUserRentals(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.getUserByEmail(email);

        // Prüfen ob User seine eigenen Rentals abruft oder Admin ist
        if (currentUser.getId().equals(id) || currentUser.getRole() == User.Role.ADMIN) {
            return ResponseEntity.ok(userService.getRentalsByUserId(id));
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    // Admin: Neuen User erstellen
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    // Admin: User aktualisieren, User: eigenes Profil aktualisieren
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userService.getUserByEmail(email);

        // Prüfen ob User sein eigenes Profil aktualisiert oder Admin ist
        if (currentUser.getId().equals(id) || currentUser.getRole() == User.Role.ADMIN) {
            return ResponseEntity.ok(userService.updateUser(id, user));
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    // Admin: User löschen
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    // Admin: Passwort zurücksetzen auf "12345."
    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> resetPassword(@PathVariable Long id) {
        User user = userService.getUserById(id);
        user.setPassword("12345.");
        return ResponseEntity.ok(userService.updateUser(id, user));
    }
}
