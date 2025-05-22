package edu.hm.cs.kreisel_backend.controller;

import edu.hm.cs.kreisel_backend.dto.AuthRequestDto;
import edu.hm.cs.kreisel_backend.service.AuthService;
import edu.hm.cs.kreisel_backend.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequestDto request) {
        var userDetails = authService.loadUserByUsername(request.getEmail());

        if (userDetails == null || !authService.checkPassword(request.getPassword(), userDetails.getPassword())) {
            return ResponseEntity.status(401).body("Ungültige Zugangsdaten");
        }

        String token = jwtUtil.generateToken(userDetails.getUsername());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequestDto request) {
        try {
            authService.registerUser(request);
            return ResponseEntity.ok("Registrierung erfolgreich");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}