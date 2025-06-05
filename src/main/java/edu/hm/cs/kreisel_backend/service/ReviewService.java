package edu.hm.cs.kreisel_backend.service;

import edu.hm.cs.kreisel_backend.dto.ItemRatingStats;
import edu.hm.cs.kreisel_backend.dto.ReviewRequest;
import edu.hm.cs.kreisel_backend.dto.ReviewResponse;
import edu.hm.cs.kreisel_backend.model.Item;
import edu.hm.cs.kreisel_backend.model.Rental;
import edu.hm.cs.kreisel_backend.model.Review;
import edu.hm.cs.kreisel_backend.model.User;
import edu.hm.cs.kreisel_backend.repository.ItemRepository;
import edu.hm.cs.kreisel_backend.repository.RentalRepository;
import edu.hm.cs.kreisel_backend.repository.ReviewRepository;
import edu.hm.cs.kreisel_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public ReviewResponse createReview(Long rentalId, ReviewRequest request) {
        // Rental finden und validieren
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental nicht gefunden"));

        // Validierungen
        validateReviewCreation(rental);

        // Review erstellen
        Review review = new Review();
        review.setRental(rental);
        review.setUser(rental.getUser());
        review.setItem(rental.getItem());
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);
        return mapToResponse(review);
    }

    private void validateReviewCreation(Rental rental) {
        // Prüfen ob Rental zurückgegeben wurde
        if (rental.getReturnDate() == null) {
            throw new RuntimeException("Item muss zurückgegeben sein, bevor es bewertet werden kann");
        }

        // Prüfen ob bereits eine Review existiert
        if (reviewRepository.existsByRentalId(rental.getId())) {
            throw new RuntimeException("Dieses Rental wurde bereits bewertet");
        }
    }

    public ReviewResponse updateReview(Long reviewId, ReviewRequest request, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review nicht gefunden"));

        // Prüfen ob User berechtigt ist, die Review zu bearbeiten
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nicht berechtigt, diese Review zu bearbeiten");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);
        return mapToResponse(review);
    }

    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review nicht gefunden"));

        // Prüfen ob User berechtigt ist, die Review zu löschen
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nicht berechtigt, diese Review zu löschen");
        }

        reviewRepository.delete(review);
    }

    public List<ReviewResponse> getReviewsByItem(Long itemId) {
        return reviewRepository.findByItemIdOrderByCreatedAtDesc(itemId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ReviewResponse getReviewByRental(Long rentalId) {
        Review review = reviewRepository.findByRentalId(rentalId)
                .orElseThrow(() -> new RuntimeException("Review nicht gefunden"));
        return mapToResponse(review);
    }

    public ItemRatingStats getItemRatingStats(Long itemId) {
        ItemRatingStats stats = new ItemRatingStats();

        // Durchschnittliche Bewertung
        Double avgRating = reviewRepository.findAverageRatingByItemId(itemId);
        stats.setAverageRating(avgRating != null ? Math.round(avgRating * 100.0) / 100.0 : 0.0);

        // Gesamtanzahl Reviews
        Integer totalReviews = reviewRepository.countReviewsByItemId(itemId);
        stats.setTotalReviews(totalReviews != null ? totalReviews : 0);

        // Rating-Verteilung
        List<Object[]> distribution = reviewRepository.findRatingDistributionByItemId(itemId);
        Map<Integer, Integer> ratingCounts = new HashMap<>();

        // Initialisiere alle Ratings mit 0
        for (int i = 1; i <= 5; i++) {
            ratingCounts.put(i, 0);
        }

        // Fülle die tatsächlichen Werte
        for (Object[] row : distribution) {
            Integer rating = (Integer) row[0];
            Long count = (Long) row[1];
            ratingCounts.put(rating, count.intValue());
        }

        stats.setRating1Count(ratingCounts.get(1));
        stats.setRating2Count(ratingCounts.get(2));
        stats.setRating3Count(ratingCounts.get(3));
        stats.setRating4Count(ratingCounts.get(4));
        stats.setRating5Count(ratingCounts.get(5));

        return stats;
    }

    public List<Long> getTopRatedItemIds(Integer minReviews) {
        if (minReviews == null) minReviews = 1;

        return reviewRepository.findTopRatedItems(minReviews)
                .stream()
                .map(row -> (Long) row[0])
                .collect(Collectors.toList());
    }

    public boolean canUserReviewRental(Long rentalId, Long userId) {
        Rental rental = rentalRepository.findById(rentalId).orElse(null);
        if (rental == null) return false;

        // User muss der Eigentümer des Rentals sein
        if (!rental.getUser().getId().equals(userId)) return false;

        // Rental muss zurückgegeben sein
        if (rental.getReturnDate() == null) return false;

        // Noch keine Review vorhanden
        return !reviewRepository.existsByRentalId(rentalId);
    }

    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setUserId(review.getUser().getId());
        response.setUserFullName(review.getUser().getFullName());
        response.setItemId(review.getItem().getId());
        response.setItemName(review.getItem().getName());
        response.setRentalId(review.getRental().getId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt().format(FORMATTER));
        response.setUpdatedAt(review.getUpdatedAt() != null ?
                review.getUpdatedAt().format(FORMATTER) : null);
        return response;
    }
}