// RegisterRequest.java
package edu.hm.cs.kreisel_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Name darf nicht leer sein")
    private String fullName;

    @NotBlank(message = "Email darf nicht leer sein")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@hm\\.edu$", message = "Nur HM-E-Mail-Adressen erlaubt")
    private String email;

    @NotBlank(message = "Passwort darf nicht leer sein")
    @Pattern(regexp = "^(?=.*[!@#$%^&*(),.?\":{}|<>])(?=\\S+$).{6,}$", 
             message = "Passwort muss mindestens 6 Zeichen lang sein und ein Sonderzeichen enthalten")
    private String password;
}
