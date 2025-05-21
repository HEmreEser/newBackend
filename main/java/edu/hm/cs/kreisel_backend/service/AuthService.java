// AuthService.java
package edu.hm.cs.kreisel_backend.service;

import edu.hm.cs.kreisel_backend.dto.AuthRequestDto;
import edu.hm.cs.kreisel_backend.dto.AuthResponseDto;
import edu.hm.cs.kreisel_backend.model.User;
import edu.hm.cs.kreisel_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Email validation pattern for HM emails
    private static final Pattern HM_EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@hm\\.edu$");

    public User registerUser(AuthRequestDto registerRequest) {
        // Validate HM email format
        if (!isValidHmEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Only HM email addresses are allowed");
        }

        // Check if user already exists
        if (userRepository.findByEmail(registerRequest.getEmail()) != null) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Create new user
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        // Set role based on email (admin or user)
        if (registerRequest.getEmail().startsWith("admin")) {
            user.setRole(User.Role.ADMIN);
        } else {
            user.setRole(User.Role.USER);
        }

        // Save user to database
        return userRepository.save(user);
    }

    private boolean isValidHmEmail(String email) {
        return email != null && HM_EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        // In a real application, you'd have password handling here
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                "password", // Replace with actual password handling
                authorities
        );
    }
}
