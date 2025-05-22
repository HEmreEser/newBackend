package edu.hm.cs.kreisel_backend.dto;

import edu.hm.cs.kreisel_backend.model.User.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private String email; // Benutzer-E-Mail
    private String token; // Authentifizierungs-Token (z. B. JWT)
    private Role role;    // Rolle des Benutzers (USER oder ADMIN)
}