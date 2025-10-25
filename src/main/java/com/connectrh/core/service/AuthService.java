package com.connectrh.core.service;

import com.connectrh.core.entity.User;
import com.connectrh.core.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service dedicated to core authentication logic, used internally by the BFF.
 * This service handles user lookup and password verification.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Attempts to validate user credentials against the database.
     *
     * @param email       The user's email (login).
     * @param rawPassword The user's plain text password.
     * @return An Optional containing the User if credentials are valid, or empty otherwise.
     */
    public Optional<User> validateCredentials(String email, String rawPassword) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            return Optional.empty(); // User not found
        }

        // Check if the provided raw password matches the encoded password in the database
        if (passwordEncoder.matches(rawPassword, user.get().getPassword())) {
            return user; // Credentials are valid
        } else {
            return Optional.empty(); // Password mismatch
        }
    }
}
