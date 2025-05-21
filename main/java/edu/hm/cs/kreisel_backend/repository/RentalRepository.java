package edu.hm.cs.kreisel_backend.repository;

import edu.hm.cs.kreisel_backend.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RentalRepository extends JpaRepository<Rental, UUID> {

    // Alle Rentals eines Benutzers abrufen
    List<Rental> findByUserId(UUID userId);

    // Alle Rentals für ein Item abrufen
    List<Rental> findByItemId(UUID itemId);

    // Alle aktiven Rentals (nicht zurückgegeben)
    List<Rental> findAllByReturnedFalse();

    // Alle aktiven Rentals eines bestimmten Benutzers
    List<Rental> findByUserIdAndReturnedFalse(UUID userId);

    // Alle abgelaufenen und nicht zurückgegebenen Rentals
    List<Rental> findAllByReturnedFalseAndEndDateBefore(LocalDate date);

    // Anzahl der aktiven Rentals eines Benutzers
    long countByUserIdAndReturnedFalse(UUID userId);
}