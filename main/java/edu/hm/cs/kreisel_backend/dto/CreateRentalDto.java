package edu.hm.cs.kreisel_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CreateRentalDto {

    @NotNull(message = "Die User-ID darf nicht null sein.")
    public UUID userId;

    @NotNull(message = "Die Item-ID darf nicht null sein.")
    public UUID itemId;

    @NotNull(message = "Das Startdatum darf nicht null sein.")
    public LocalDate startDate;

    @NotNull(message = "Das Enddatum darf nicht null sein.")
    public LocalDate endDate;
}