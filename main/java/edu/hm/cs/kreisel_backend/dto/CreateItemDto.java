package edu.hm.cs.kreisel_backend.dto;

import edu.hm.cs.kreisel_backend.model.Item;

import java.time.LocalDate;
import java.util.UUID;

public class CreateItemDto {
    public String name;
    public String description;
    public String brand;
    public LocalDate availableFrom;
    public String imageUrl;
    public Item.Size size;
    public Item.Gender gender;
    public Item.Condition condition;
    public Item.Status status;
    public Item.Location location;
    public UUID categoryId;
    public UUID subcategoryId;
}
