
// AuthRequestDto.java
package edu.hm.cs.kreisel_backend.dto;

import lombok.Data;
@Data
public class AuthRequestDto {
    private String email;
    private String password;

    // Konstruktor für Lombok ergänzen
    public AuthRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}