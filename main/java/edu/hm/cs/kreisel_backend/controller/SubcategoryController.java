package edu.hm.cs.kreisel_backend.controller;

import edu.hm.cs.kreisel_backend.model.*;
import edu.hm.cs.kreisel_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/subcategories")
public class SubcategoryController {

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @GetMapping
    public List<Subcategory> getAllSubcategories() {
        return subcategoryRepository.findAll();
    }
}
