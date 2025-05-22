package edu.hm.cs.kreisel_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    private UUID id = UUID.randomUUID();

    @Email(regexp = "^[a-zA-Z0-9._%+-]+@hm\\.edu$", message = "Nur hm.edu Emails erlaubt")
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;  // neu

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Rental> rentals;

    public boolean canRent() {
        return rentals == null || rentals.stream().filter(Rental::isActive).count() < 5;
    }

    public enum Role {
        USER,
        ADMIN
    }
}
