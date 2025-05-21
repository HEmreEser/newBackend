package edu.hm.cs.kreisel_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Entity
@Getter
@Setter
@Table(name = "rentals")
public class Rental {

    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private LocalDate startDate;
    private LocalDate endDate;
    private boolean returned;
    private LocalDate returnedAt;

    public boolean isActive() {
        return !returned && LocalDate.now().isBefore(endDate);
    }
}
