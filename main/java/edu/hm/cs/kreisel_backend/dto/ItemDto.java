package edu.hm.cs.kreisel_backend.dto;

import edu.hm.cs.kreisel_backend.model.Item.*;
import edu.hm.cs.kreisel_backend.model.User.Role;

import java.time.LocalDate;
import java.util.UUID;

public class ItemDto {
    public UUID id;
    public String name;
    public String description;
    public String brand;
    public LocalDate availableFrom;
    public String imageUrl;
    public Size size;
    public Gender gender;
    public Condition condition;
    public Status status;
    public Location location;
    public UUID categoryId;
    public UUID subcategoryId;
}
