package edu.hm.cs.kreisel_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest {

    @NotNull(message = "Rating ist erforderlich")
    @Min(value = 1, message = "Rating muss zwischen 1 und 5 liegen")
    @Max(value = 5, message = "Rating muss zwischen 1 und 5 liegen")
    private Integer rating;

    private String comment;
}
