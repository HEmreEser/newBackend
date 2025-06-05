package edu.hm.cs.kreisel_backend.controller;

import edu.hm.cs.kreisel_backend.model.Item;
import edu.hm.cs.kreisel_backend.model.Item.*;
import edu.hm.cs.kreisel_backend.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // Haupt-GET-Endpunkt mit allen Filtern
    @GetMapping
    public ResponseEntity<List<Item>> getFilteredItems(
            @RequestParam Location location,                    // Pflicht: Standort
            @RequestParam(required = false) Boolean available,   // Optional: Verfügbarkeit
            @RequestParam(required = false) String searchQuery,  // Optional: Textsuche
            @RequestParam(required = false) Gender gender,       // Optional: Gender
            @RequestParam(required = false) Category category,   // Optional: Kategorie
            @RequestParam(required = false) Subcategory subcategory, // Optional: Unterkategorie
            @RequestParam(required = false) String size,         // Optional: Größe
            @RequestParam(required = false) Boolean sortByRating // Optional: Nach Bewertung sortieren
    ) {
        return ResponseEntity.ok(itemService.filterItems(
                location,
                available,
                searchQuery,
                gender,
                category,
                subcategory,
                size,
                sortByRating
        ));
    }

    // Item nach ID abrufen
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    // Admin: Neues Item erstellen
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item) {
        return ResponseEntity.ok(itemService.createItem(item));
    }

    // Admin: Item aktualisieren
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @Valid @RequestBody Item item) {
        return ResponseEntity.ok(itemService.updateItem(id, item));
    }

    // Admin: Item löschen
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok().build();
    }
}
