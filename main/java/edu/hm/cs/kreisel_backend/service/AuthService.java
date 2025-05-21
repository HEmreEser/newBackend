package edu.hm.cs.kreisel_backend.service;

import edu.hm.cs.kreisel_backend.dto.AuthRequestDto;
import edu.hm.cs.kreisel_backend.model.User;
import edu.hm.cs.kreisel_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Pattern HM_EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@hm\\.edu$");

    public User registerUser(AuthRequestDto registerRequest) {
        if (!isValidHmEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Nur HM-E-Mail-Adressen sind erlaubt");
        }

        if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 6) {
            throw new IllegalArgumentException("Passwort muss mindestens 6 Zeichen lang sein");
        }

        if (userRepository.findByEmail(registerRequest.getEmail()) != null) {
            throw new IllegalArgumentException("Benutzer mit dieser E-Mail existiert bereits");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        if (registerRequest.getEmail().startsWith("admin")) {
            user.setRole(User.Role.ADMIN);
        } else {
            user.setRole(User.Role.USER);
        }

        return userRepository.save(user);
    }

    private boolean isValidHmEmail(String email) {
        return email != null && HM_EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public org.springframework.security.core.userdetails.User loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Benutzer nicht gefunden mit E-Mail: " + email);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                java.util.Collections.emptyList()
        );
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
