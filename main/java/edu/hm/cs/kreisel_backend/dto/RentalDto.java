package edu.hm.cs.kreisel_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class RentalDto {

    private UUID id;
    private UUID userId;
    private UUID itemId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean returned;
    private LocalDate returnedAt; // Zeitpunkt der Rückgabe
}