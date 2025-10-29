package com.connectrh.core.service;

import com.connectrh.core.dto.request.CreateUserRequest;
import com.connectrh.core.entity.Role;
import com.connectrh.core.entity.User;
import com.connectrh.core.repository.RoleRepository;
import com.connectrh.core.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Service dedicated to core authentication logic, used internally by the BFF.
 * This service handles user lookup and password verification.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
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

    public User createUser(CreateUserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("O email '" + request.getEmail() + "' já está cadastrado.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(encodedPassword);
        newUser.setPhoneNumber(request.getPhoneNumber());

        Role.RoleName defaultRoleName = Role.RoleName.valueOf("USER");

        Role defaultRole = roleRepository.findByName(defaultRoleName)
                .orElseThrow(() -> new RuntimeException("Erro de configuração: Role '" + defaultRoleName + "' não encontrada."));
        newUser.setRoles(new HashSet<>(Set.of(defaultRole)));

        return userRepository.save(newUser);
    }
}
