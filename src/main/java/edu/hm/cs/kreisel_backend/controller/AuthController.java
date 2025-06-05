package edu.hm.cs.kreisel_backend.controller;

import edu.hm.cs.kreisel_backend.dto.LoginRequest;
import edu.hm.cs.kreisel_backend.dto.RegisterRequest;
import edu.hm.cs.kreisel_backend.dto.AuthResponse;
import edu.hm.cs.kreisel_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
//@preauthorize
    //jeder soll sich registrieren können
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    //jeder soll sich anmelden könnnen
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    //jeder soll sich abmelden können
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        authService.logout(token);
        return ResponseEntity.ok("Logout erfolgreich");
    }
}
