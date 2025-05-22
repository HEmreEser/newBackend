package edu.hm.cs.kreisel_backend.controller;

import edu.hm.cs.kreisel_backend.dto.CreateItemDto;
import edu.hm.cs.kreisel_backend.dto.ItemDto;
import edu.hm.cs.kreisel_backend.model.Item;
import edu.hm.cs.kreisel_backend.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable UUID id) {
        return itemService.getItemById(id);
    }

    @GetMapping("/location/{location}")
    public List<ItemDto> getItemsByLocation(@PathVariable Item.Location location) {
        return itemService.getItemsByLocation(location);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ItemDto createItem(@RequestBody CreateItemDto createItemDto) {
        return itemService.createItem(createItemDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ItemDto updateItem(@PathVariable UUID id, @RequestBody CreateItemDto updateItemDto) {
        return itemService.updateItem(id, updateItemDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteItem(@PathVariable UUID id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok().build();
    }

    // 🔎 Suche mit optionalen Parametern
    @GetMapping("/search")
    public List<ItemDto> searchItems(
            @RequestParam Optional<String> searchTerm,
            @RequestParam Optional<Item.Gender> gender,
            @RequestParam Optional<UUID> categoryId,
            @RequestParam Optional<UUID> subcategoryId,
            @RequestParam Optional<String> size,
            @RequestParam Optional<Item.Status> status
    ) {
        return itemService.searchItems(searchTerm, gender, categoryId, subcategoryId, size, status);
    }
}
