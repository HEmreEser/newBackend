package edu.hm.cs.kreisel_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemRatingStats {
    private Double averageRating;
    private Integer totalReviews;
    private Integer rating1Count;
    private Integer rating2Count;
    private Integer rating3Count;
    private Integer rating4Count;
    private Integer rating5Count;
}