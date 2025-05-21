package edu.hm.cs.kreisel_backend.controller;

import edu.hm.cs.kreisel_backend.model.Item;
import edu.hm.cs.kreisel_backend.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @GetMapping
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @GetMapping("/location/{location}")
    public List<Item> getItemsByLocation(@PathVariable Item.Location location) {
        return itemRepository.findByLocation(location);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Item createItem(@RequestBody Item item) {
        return itemRepository.save(item);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Item updateItem(@PathVariable UUID id, @RequestBody Item updatedItem) {
        Item item = itemRepository.findById(id).orElseThrow();
        item.setName(updatedItem.getName());
        item.setDescription(updatedItem.getDescription());
        item.setBrand(updatedItem.getBrand());
        item.setAvailableFrom(updatedItem.getAvailableFrom());
        item.setImageUrl(updatedItem.getImageUrl());
        item.setSize(updatedItem.getSize());
        item.setGender(updatedItem.getGender());
        item.setCondition(updatedItem.getCondition());
        item.setStatus(updatedItem.getStatus());
        item.setLocation(updatedItem.getLocation());
        item.setCategory(updatedItem.getCategory());
        item.setSubcategory(updatedItem.getSubcategory());
        return itemRepository.save(item);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteItem(@PathVariable UUID id) {
        itemRepository.deleteById(id);
    }
}
