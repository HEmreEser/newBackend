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
@Table(name = "items")
public class Item {

    @Id
    private UUID id = UUID.randomUUID();

    private String name;
    private String description;
    private String brand;
    private LocalDate availableFrom;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Size size;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Condition condition;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Location location;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "subcategory_id")
    private Subcategory subcategory;

    public boolean isAvailable() {
        return status == Status.Verfugbar && LocalDate.now().isAfter(availableFrom);
    }

    public enum Size {
        XS, S, M, L, XL
    }

    public enum Gender {
        Damen, Herren
    }

    public enum Condition {
        Neu, Gebraucht
    }

    public enum Status {
        Verfugbar, NichtVerfugbar
    }

    public enum Location {
        Lothstraße, Pasing, Karlstraße
    }

}
