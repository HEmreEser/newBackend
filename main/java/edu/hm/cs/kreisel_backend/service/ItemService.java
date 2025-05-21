package edu.hm.cs.kreisel_backend.service;

import edu.hm.cs.kreisel_backend.dto.CreateItemDto;
import edu.hm.cs.kreisel_backend.dto.ItemDto;
import edu.hm.cs.kreisel_backend.model.Category;
import edu.hm.cs.kreisel_backend.model.Item;
import edu.hm.cs.kreisel_backend.model.Subcategory;
import edu.hm.cs.kreisel_backend.repository.CategoryRepository;
import edu.hm.cs.kreisel_backend.repository.ItemRepository;
import edu.hm.cs.kreisel_backend.repository.SubcategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    public List<ItemDto> getAllItems() {
        return itemRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> getItemsByLocation(Item.Location location) {
        return itemRepository.findByLocation(location).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(UUID id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        return convertToDto(item);
    }

    public ItemDto createItem(CreateItemDto createItemDto) {
        Item item = new Item();
        item.setName(createItemDto.name);
        item.setDescription(createItemDto.description);
        item.setBrand(createItemDto.brand);
        item.setAvailableFrom(createItemDto.availableFrom);
        item.setImageUrl(createItemDto.imageUrl);
        item.setSize(createItemDto.size);
        item.setGender(createItemDto.gender);
        item.setCondition(createItemDto.condition);
        item.setStatus(createItemDto.status);
        item.setLocation(createItemDto.location);

        if (createItemDto.categoryId != null) {
            Category category = categoryRepository.findById(createItemDto.categoryId).orElse(null);
            item.setCategory(category);
        }

        if (createItemDto.subcategoryId != null) {
            Subcategory subcategory = subcategoryRepository.findById(createItemDto.subcategoryId).orElse(null);
            item.setSubcategory(subcategory);
        }

        Item savedItem = itemRepository.save(item);
        return convertToDto(savedItem);
    }

    public ItemDto updateItem(UUID id, CreateItemDto updateDto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item nicht gefunden"));

        item.setName(updateDto.name);
        item.setDescription(updateDto.description);
        item.setBrand(updateDto.brand);
        item.setAvailableFrom(updateDto.availableFrom);
        item.setImageUrl(updateDto.imageUrl);
        item.setSize(updateDto.size);
        item.setGender(updateDto.gender);
        item.setCondition(updateDto.condition);
        item.setStatus(updateDto.status);
        item.setLocation(updateDto.location);

        if (updateDto.categoryId != null) {
            Category category = categoryRepository.findById(updateDto.categoryId).orElse(null);
            item.setCategory(category);
        } else {
            item.setCategory(null);
        }

        if (updateDto.subcategoryId != null) {
            Subcategory subcategory = subcategoryRepository.findById(updateDto.subcategoryId).orElse(null);
            item.setSubcategory(subcategory);
        } else {
            item.setSubcategory(null);
        }

        Item savedItem = itemRepository.save(item);
        return convertToDto(savedItem);
    }

    public void deleteItem(UUID id) {
        if (itemRepository.existsById(id)) {
            itemRepository.deleteById(id);
        }
    }

    public List<ItemDto> searchItems(
            Optional<String> searchTerm,
            Optional<Item.Gender> gender,
            Optional<UUID> categoryId,
            Optional<UUID> subcategoryId,
            Optional<String> size,
            Optional<Item.Status> status
    ) {
        List<Item> items = itemRepository.findAll();

        return items.stream()
                .filter(item -> {
                    if (searchTerm.isPresent()) {
                        String term = searchTerm.get().toLowerCase();
                        boolean matches = (item.getName() != null && item.getName().toLowerCase().contains(term))
                                || (item.getDescription() != null && item.getDescription().toLowerCase().contains(term))
                                || (item.getBrand() != null && item.getBrand().toLowerCase().contains(term));
                        if (!matches) return false;
                    }

                    if (gender.isPresent() && item.getGender() != gender.get()) {
                        return false;
                    }

                    if (categoryId.isPresent()) {
                        if (item.getCategory() == null || !item.getCategory().getId().equals(categoryId.get())) {
                            return false;
                        }
                    }

                    if (subcategoryId.isPresent()) {
                        if (item.getSubcategory() == null || !item.getSubcategory().getId().equals(subcategoryId.get())) {
                            return false;
                        }
                        if (categoryId.isPresent()) {
                            UUID catId = categoryId.get();
                            if (item.getSubcategory().getCategory() == null || !item.getSubcategory().getCategory().getId().equals(catId)) {
                                return false;
                            }
                        }
                    }

                    if (size.isPresent()) {
                        try {
                            Item.Size filterSize = Item.Size.valueOf(size.get().toUpperCase());
                            if (item.getSize() != filterSize) {
                                return false;
                            }
                        } catch (IllegalArgumentException e) {
                            return false;
                        }
                    }

                    if (status.isPresent() && item.getStatus() != status.get()) {
                        return false;
                    }

                    return true;
                })
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ItemDto convertToDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.id = item.getId();
        dto.name = item.getName();
        dto.description = item.getDescription();
        dto.brand = item.getBrand();
        dto.availableFrom = item.getAvailableFrom();
        dto.imageUrl = item.getImageUrl();
        dto.size = item.getSize();
        dto.gender = item.getGender();
        dto.condition = item.getCondition();
        dto.status = item.getStatus();
        dto.location = item.getLocation();

        if (item.getCategory() != null) {
            dto.categoryId = item.getCategory().getId();
        }

        if (item.getSubcategory() != null) {
            dto.subcategoryId = item.getSubcategory().getId();
        }

        return dto;
    }
}
