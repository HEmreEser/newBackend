package edu.hm.cs.kreisel_backend.repository;

import edu.hm.cs.kreisel_backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SubcategoryRepository extends JpaRepository<Subcategory, UUID> {
    Subcategory findByName(String name);
}