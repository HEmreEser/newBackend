package edu.hm.cs.kreisel_backend.dto;

import edu.hm.cs.kreisel_backend.model.Item.*;
import edu.hm.cs.kreisel_backend.model.User.Role;

import java.time.LocalDate;
import java.util.UUID;

public class RentalDto {
    public UUID id;
    public UUID userId;
    public UUID itemId;
    public LocalDate startDate;
    public LocalDate endDate;
    public boolean returned;
    public LocalDate returnedAt;
}