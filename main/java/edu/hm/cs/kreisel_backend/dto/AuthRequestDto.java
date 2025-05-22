
// AuthRequestDto.java
package edu.hm.cs.kreisel_backend.dto;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
public class AuthRequestDto {
    private String email;
    private String password;

    public AuthRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}