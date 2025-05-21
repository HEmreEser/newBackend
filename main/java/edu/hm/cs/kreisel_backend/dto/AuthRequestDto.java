
// AuthRequestDto.java
package edu.hm.cs.kreisel_backend.dto;

import lombok.Data;

@Data
public class AuthRequestDto {
    private String email;
    private String password; // For future password implementation
}
