// ItemService.java
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

        // Set category and subcategory
        if (createItemDto.categoryId != null) {
            Category category = categoryRepository.findById(createItemDto.categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            item.setCategory(category);
        }

        if (createItemDto.subcategoryId != null) {
            Subcategory subcategory = subcategoryRepository.findById(createItemDto.subcategoryId)
                    .orElseThrow(() -> new RuntimeException("Subcategory not found"));
            item.setSubcategory(subcategory);
        }

        Item savedItem = itemRepository.save(item);
        return convertToDto(savedItem);
    }

    public ItemDto updateItem(UUID id, CreateItemDto updateItemDto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setName(updateItemDto.name);
        item.setDescription(updateItemDto.description);
        item.setBrand(updateItemDto.brand);
        item.setAvailableFrom(updateItemDto.availableFrom);
        item.setImageUrl(updateItemDto.imageUrl);
        item.setSize(updateItemDto.size);
        item.setGender(updateItemDto.gender);
        item.setCondition(updateItemDto.condition);
        item.setStatus(updateItemDto.status);
        item.setLocation(updateItemDto.location);

        // Update category and subcategory
        if (updateItemDto.categoryId != null) {
            Category category = categoryRepository.findById(updateItemDto.categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            item.setCategory(category);
        }

        if (updateItemDto.subcategoryId != null) {
            Subcategory subcategory = subcategoryRepository.findById(updateItemDto.subcategoryId)
                    .orElseThrow(() -> new RuntimeException("Subcategory not found"));
            item.setSubcategory(subcategory);
        }

        Item updatedItem = itemRepository.save(item);
        return convertToDto(updatedItem);
    }

    public void deleteItem(UUID id) {
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("Item not found");
        }
        itemRepository.deleteById(id);
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
