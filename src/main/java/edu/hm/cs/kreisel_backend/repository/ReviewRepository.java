package edu.hm.cs.kreisel_backend.repository;

import edu.hm.cs.kreisel_backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Review für ein spezifisches Rental finden
    Optional<Review> findByRentalId(Long rentalId);

    // Alle Reviews für ein Item
    List<Review> findByItemIdOrderByCreatedAtDesc(Long itemId);

    // Alle Reviews von einem User
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Prüfen ob Review für Rental bereits existiert
    boolean existsByRentalId(Long rentalId);

    // Durchschnittliche Bewertung für ein Item
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.item.id = :itemId")
    Double findAverageRatingByItemId(@Param("itemId") Long itemId);

    // Anzahl Reviews für ein Item
    @Query("SELECT COUNT(r) FROM Review r WHERE r.item.id = :itemId")
    Integer countReviewsByItemId(@Param("itemId") Long itemId);

    // Rating-Verteilung für ein Item
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.item.id = :itemId GROUP BY r.rating")
    List<Object[]> findRatingDistributionByItemId(@Param("itemId") Long itemId);

    // Top bewertete Items
    @Query("SELECT r.item.id, AVG(r.rating) as avgRating, COUNT(r) as reviewCount " +
            "FROM Review r " +
            "GROUP BY r.item.id " +
            "HAVING COUNT(r) >= :minReviews " +
            "ORDER BY avgRating DESC")
    List<Object[]> findTopRatedItems(@Param("minReviews") Integer minReviews);
}