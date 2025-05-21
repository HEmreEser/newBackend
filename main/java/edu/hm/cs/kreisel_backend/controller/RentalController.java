package edu.hm.cs.kreisel_backend.controller;

import edu.hm.cs.kreisel_backend.model.*;
import edu.hm.cs.kreisel_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    @Autowired
    private RentalRepository rentalRepository;

    @GetMapping("/user/{userId}")
    public List<Rental> getRentalsByUser(@PathVariable UUID userId) {
        return rentalRepository.findByUserId(userId);
    }

    @GetMapping("/item/{itemId}")
    public List<Rental> getRentalsByItem(@PathVariable UUID itemId) {
        return rentalRepository.findByItemId(itemId);
    }
}