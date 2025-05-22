package edu.hm.cs.kreisel_backend.dto;

import edu.hm.cs.kreisel_backend.model.User.Role;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor

public class UserDto {
    public UUID id;
    public String email;
    public Role role;
}
