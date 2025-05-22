package edu.hm.cs.kreisel_backend.dto;

import edu.hm.cs.kreisel_backend.model.Item;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ItemDto {
    private UUID id;
    private String name;
    private String description;
    private String brand;
    private LocalDate availableFrom;
    private String imageUrl;
    private Item.Size size;
    private Item.Gender gender;
    private Item.Condition condition;
    private Item.Status status;
    private Item.Location location;
    private UUID categoryId;
    private UUID subcategoryId;
}