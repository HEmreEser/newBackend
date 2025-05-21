// AuthResponseDto.java
package edu.hm.cs.kreisel_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private String email;
    private String token; // For JWT implementation
    private String role;
}