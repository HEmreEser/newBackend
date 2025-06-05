package edu.hm.cs.kreisel_backend.controller;

import edu.hm.cs.kreisel_backend.dto.ItemRatingStats;
import edu.hm.cs.kreisel_backend.dto.ReviewRequest;
import edu.hm.cs.kreisel_backend.dto.ReviewResponse;
import edu.hm.cs.kreisel_backend.model.User;
import edu.hm.cs.kreisel_backend.service.ReviewService;
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
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    // Review für ein Rental erstellen
    @PostMapping("/rental/{rentalId}")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long rentalId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.createReview(rentalId, request));
    }

    // Review bearbeiten
    @PutMapping("/{reviewId}/user/{userId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @PathVariable Long userId,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, request, userId));
    }

    // Review löschen
    @DeleteMapping("/{reviewId}/user/{userId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable Long reviewId,
            @PathVariable Long userId) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok("Review erfolgreich gelöscht");
    }

    // Alle Reviews für ein Item
    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(reviewService.getReviewsByItem(itemId));
    }

    // Alle Reviews von einem User
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    // Review für ein bestimmtes Rental abrufen
    @GetMapping("/rental/{rentalId}")
    public ResponseEntity<ReviewResponse> getReviewByRental(@PathVariable Long rentalId) {
        return ResponseEntity.ok(reviewService.getReviewByRental(rentalId));
    }

    // Rating-Statistiken für ein Item
    @GetMapping("/item/{itemId}/stats")
    public ResponseEntity<ItemRatingStats> getItemRatingStats(@PathVariable Long itemId) {
        return ResponseEntity.ok(reviewService.getItemRatingStats(itemId));
    }

    // Top bewertete Items
    @GetMapping("/top-items")
    public ResponseEntity<List<Long>> getTopRatedItems(
            @RequestParam(defaultValue = "3") Integer minReviews) {
        return ResponseEntity.ok(reviewService.getTopRatedItemIds(minReviews));
    }

    // Prüfen ob User ein Rental bewerten kann
    @GetMapping("/rental/{rentalId}/can-review/user/{userId}")
    public ResponseEntity<Boolean> canUserReviewRental(
            @PathVariable Long rentalId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.canUserReviewRental(rentalId, userId));
    }
}
