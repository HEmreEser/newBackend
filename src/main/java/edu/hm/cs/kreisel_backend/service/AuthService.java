package edu.hm.cs.kreisel_backend.service;

import edu.hm.cs.kreisel_backend.dto.AuthResponse;
import edu.hm.cs.kreisel_backend.dto.LoginRequest;
import edu.hm.cs.kreisel_backend.dto.RegisterRequest;
import edu.hm.cs.kreisel_backend.model.User;
import edu.hm.cs.kreisel_backend.repository.UserRepository;
import edu.hm.cs.kreisel_backend.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Validated
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Password validation pattern: at least 6 characters and one special character
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[!@#$%^&*(),.?\":{}|<>])(?=\\S+$).{6,}$");

    public AuthResponse register(@Valid RegisterRequest request) {
        // Prüfen ob Email bereits existiert
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email bereits registriert");
        }

        // HM Email validieren
        if (!request.getEmail().endsWith("@hm.edu")) {
            throw new RuntimeException("Nur HM-E-Mail-Adressen sind erlaubt");
        }

        // Passwort validieren
        if (!PASSWORD_PATTERN.matcher(request.getPassword()).matches()) {
            throw new RuntimeException("Passwort muss mindestens 6 Zeichen lang sein und ein Sonderzeichen enthalten");
        }

        // Neuen User erstellen
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Auto-Admin für admin-emails
        if (request.getEmail().toLowerCase().startsWith("admin")) {
            user.setRole(User.Role.ADMIN);
        } else {
            user.setRole(User.Role.USER);
        }

        user = userRepository.save(user);

        // JWT Token generieren
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().toString())
                .token(token)
                .message("Registrierung erfolgreich")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email oder Passwort falsch"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email oder Passwort falsch");
        }

        // JWT Token generieren
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().toString())
                .token(token)
                .message("Login erfolgreich")
                .build();
    }

    /**
     * Invalidiert ein JWT Token (Logout)
     * @param token Das zu invalidierende Token
     */
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            jwtUtil.invalidateToken(token);
        }
    }
}
