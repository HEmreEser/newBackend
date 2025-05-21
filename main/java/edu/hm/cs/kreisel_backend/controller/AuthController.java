package edu.hm.cs.kreisel_backend.controller;

import edu.hm.cs.kreisel_backend.dto.AuthRequestDto;
import edu.hm.cs.kreisel_backend.service.AuthService;
import edu.hm.cs.kreisel_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public String login(@RequestBody AuthRequestDto request) {
        var userDetails = authService.loadUserByUsername(request.getEmail());

        if (userDetails == null || !authService.checkPassword(request.getPassword(), userDetails.getPassword())) {
            throw new RuntimeException("Ungültige Zugangsdaten");
        }

        return jwtUtil.generateToken(userDetails.getUsername());
    }

    @PostMapping("/register")
    public String register(@RequestBody AuthRequestDto request) {
        authService.registerUser(request);
        return "Registrierung erfolgreich";
    }
}

