package edu.hm.cs.kreisel_backend.repository;

import edu.hm.cs.kreisel_backend.model.Item;
import edu.hm.cs.kreisel_backend.model.Item.Gender;
import edu.hm.cs.kreisel_backend.model.Item.Location;
import edu.hm.cs.kreisel_backend.model.Item.Size;
import edu.hm.cs.kreisel_backend.model.Item.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    // Alle Items an einem bestimmten Standort
    List<Item> findByLocation(Location location);

    // Alle Items nach Status (z.B. Verfügbar, NichtVerfügbar)
    List<Item> findByStatus(Status status);

    // Items nach Größe (z.B. XS, M, L)
    List<Item> findBySize(Size size);

    // Items nach Gender (Damen, Herren)
    List<Item> findByGender(Gender gender);

    // Kombination von Standort und Status
    List<Item> findByLocationAndStatus(Location location, Status status);

    // Freitextsuche im Namen (Case-Insensitive)
    List<Item> findByNameContainingIgnoreCase(String name);

    // Suche nach Kategorie (falls benötigt)
    List<Item> findByCategoryId(UUID categoryId);

    // Suche nach Subkategorie (falls benötigt)
    List<Item> findBySubcategoryId(UUID subcategoryId);

    // Freitextsuche mit Filter nach Standort und Größe
    List<Item> findByNameContainingIgnoreCaseAndLocationAndSize(String name, Location location, Size size);

    // Filter nach Subkategorie und Status
    List<Item> findBySubcategoryIdAndStatus(UUID subcategoryId, Status status);
}