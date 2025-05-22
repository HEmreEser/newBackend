package edu.hm.cs.kreisel_backend.dto;

import edu.hm.cs.kreisel_backend.model.Item;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemDto {

    @NotNull(message = "Der Name des Items darf nicht null sein.")
    @Size(min = 2, max = 255, message = "Der Name des Items muss zwischen 2 und 255 Zeichen lang sein.")
    private String name;

    @Size(max = 500, message = "Die Beschreibung darf maximal 500 Zeichen enthalten.")
    private String description;

    @Size(max = 100, message = "Die Marke darf maximal 100 Zeichen enthalten.")
    private String brand;

    private LocalDate availableFrom;

    @Size(max = 2048, message = "Die Bild-URL darf maximal 2048 Zeichen enthalten.")
    private String imageUrl;

    private Item.Size size;

    private Item.Gender gender;

    private Item.Condition condition;

    @NotNull(message = "Der Status des Items darf nicht null sein.")
    private Item.Status status;

    @NotNull(message = "Der Standort des Items darf nicht null sein.")
    private Item.Location location;

    private UUID categoryId;
    private UUID subcategoryId;
}