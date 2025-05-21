package edu.hm.cs.kreisel_backend.dto;

import java.time.LocalDate;
import java.util.UUID;


public class CreateRentalDto {
    public UUID userId;
    public UUID itemId;
    public LocalDate startDate;
    public LocalDate endDate;
}