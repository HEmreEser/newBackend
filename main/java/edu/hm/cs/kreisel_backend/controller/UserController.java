package edu.hm.cs.kreisel_backend.controller;

import edu.hm.cs.kreisel_backend.dto.UserDto;
import edu.hm.cs.kreisel_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Admin kann alle User sehen
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    // User kann nur sich selbst abfragen
    @GetMapping("/me")
    public UserDto getCurrentUser(Authentication authentication) {
        // Email aus dem Authentication-Principal holen
        String email = authentication.getName();
        return userService.getUserByEmail(email);
    }

    // Optional: User kann sich selbst über ID abfragen, aber nur, wenn es der eigene User ist oder Admin
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isUserIdMatching(authentication, #id)")
    public UserDto getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }
}
