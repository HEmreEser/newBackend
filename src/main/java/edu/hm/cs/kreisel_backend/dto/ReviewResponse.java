package edu.hm.cs.kreisel_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ReviewResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private Long itemId;
    private String itemName;
    private Long rentalId;
    private Integer rating;
    private String comment;
    private String createdAt;
    private String updatedAt;
}
