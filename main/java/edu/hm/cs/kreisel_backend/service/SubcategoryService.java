// SubcategoryService.java
package edu.hm.cs.kreisel_backend.service;

import edu.hm.cs.kreisel_backend.dto.CreateSubcategoryDto;
import edu.hm.cs.kreisel_backend.dto.SubcategoryDto;
import edu.hm.cs.kreisel_backend.model.Subcategory;
import edu.hm.cs.kreisel_backend.repository.SubcategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubcategoryService {

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    public List<SubcategoryDto> getAllSubcategories() {
        return subcategoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public SubcategoryDto getSubcategoryById(UUID id) {
        Subcategory subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subcategory not found"));
        return convertToDto(subcategory);
    }

    public SubcategoryDto createSubcategory(CreateSubcategoryDto createSubcategoryDto) {
        // Check if name already exists
        if (subcategoryRepository.findByName(createSubcategoryDto.name) != null) {
            throw new RuntimeException("Subcategory with this name already exists");
        }

        Subcategory subcategory = new Subcategory();
        subcategory.setName(createSubcategoryDto.name);

        Subcategory savedSubcategory = subcategoryRepository.save(subcategory);
        return convertToDto(savedSubcategory);
    }

    public SubcategoryDto updateSubcategory(UUID id, CreateSubcategoryDto updateSubcategoryDto) {
        Subcategory subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subcategory not found"));

        // Check if new name already exists for another subcategory
        Subcategory existingSubcategory = subcategoryRepository.findByName(updateSubcategoryDto.name);
        if (existingSubcategory != null && !existingSubcategory.getId().equals(id)) {
            throw new RuntimeException("Subcategory with this name already exists");
        }

        subcategory.setName(updateSubcategoryDto.name);
        Subcategory updatedSubcategory = subcategoryRepository.save(subcategory);
        return convertToDto(updatedSubcategory);
    }

    public void deleteSubcategory(UUID id) {
        if (!subcategoryRepository.existsById(id)) {
            throw new RuntimeException("Subcategory not found");
        }
        subcategoryRepository.deleteById(id);
    }

    private SubcategoryDto convertToDto(Subcategory subcategory) {
        SubcategoryDto dto = new SubcategoryDto();
        dto.id = subcategory.getId();
        dto.name = subcategory.getName();
        return dto;
    }
}
