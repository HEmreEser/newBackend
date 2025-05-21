package edu.hm.cs.kreisel_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "subcategories")
public class Subcategory {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(optional = false) // Jede Subcategory braucht genau eine Category
    @JoinColumn(name = "category_id", nullable = false) // Foreign Key Spalte
    private Category category;
}
