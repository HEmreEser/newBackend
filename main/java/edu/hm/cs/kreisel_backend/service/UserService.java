
// UserService.java
package edu.hm.cs.kreisel_backend.service;

import edu.hm.cs.kreisel_backend.dto.CreateUserDto;
import edu.hm.cs.kreisel_backend.dto.UserDto;
import edu.hm.cs.kreisel_backend.model.User;
import edu.hm.cs.kreisel_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Email validation pattern for HM emails
    private static final Pattern HM_EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@hm\\.edu$");

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDto(user);
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return convertToDto(user);
    }

    public UserDto createUser(CreateUserDto createUserDto) {
        // Validate HM email format
        if (!isValidHmEmail(createUserDto.email)) {
            throw new RuntimeException("Only HM email addresses are allowed");
        }

        // Check if user already exists
        if (userRepository.findByEmail(createUserDto.email) != null) {
            throw new RuntimeException("User with this email already exists");
        }

        User user = new User();
        user.setEmail(createUserDto.email);
        user.setRole(createUserDto.role);

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    public UserDto updateUser(UUID id, CreateUserDto updateUserDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate HM email format
        if (!isValidHmEmail(updateUserDto.email)) {
            throw new RuntimeException("Only HM email addresses are allowed");
        }

        // Check if email already exists for another user
        User existingUser = userRepository.findByEmail(updateUserDto.email);
        if (existingUser != null && !existingUser.getId().equals(id)) {
            throw new RuntimeException("User with this email already exists");
        }

        user.setEmail(updateUserDto.email);
        user.setRole(updateUserDto.role);

        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    public boolean canUserRent(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.canRent();
    }

    private boolean isValidHmEmail(String email) {
        return email != null && HM_EMAIL_PATTERN.matcher(email).matches();
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.id = user.getId();
        dto.email = user.getEmail();
        dto.role = user.getRole();
        return dto;
    }
}