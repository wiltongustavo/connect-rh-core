package com.connectrh.core.data;

import com.connectrh.core.entity.Role;
import com.connectrh.core.entity.Role.RoleName;
import com.connectrh.core.entity.User;
import com.connectrh.core.repository.RoleRepository;
import com.connectrh.core.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

/**
 * Initializes the database with base roles and a default ADMIN user upon application startup.
 * Implements CommandLineRunner to run logic once the application context is loaded.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Dependency injection via constructor
    public DataLoader(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Ensure roles exist
        Role adminRole = createRoleIfNotFound(RoleName.ADMIN);
        Role managerRole = createRoleIfNotFound(RoleName.MANAGER);
        createRoleIfNotFound(RoleName.EMPLOYEE); // Create the base employee role

        // Create default ADMIN user if none exists
        if (userRepository.findByEmail("admin@connectrh.com").isEmpty()) {
            User adminUser = new User();
            adminUser.setName("System Administrator");
            adminUser.setEmail("admin@connectrh.com");
            adminUser.setPhoneNumber("11954444380");
            // NOTE: Change this password in production!
            adminUser.setPassword(passwordEncoder.encode("admin123"));

            // Assign ADMIN role
            adminUser.setRoles(Set.of(adminRole, managerRole)); // Admin often has management permissions too
            userRepository.save(adminUser);

            System.out.println("--- ADMIN User created: admin@connectrh.com / admin123 (CHANGE ME!) ---");
        }
    }

    /**
     * Helper method to create a Role if it doesn't already exist in the database.
     */
    private Role createRoleIfNotFound(RoleName name) {
        Optional<Role> role = roleRepository.findByName(name);
        if (role.isEmpty()) {
            Role newRole = new Role();
            newRole.setName(name);
            return roleRepository.save(newRole);
        }
        return role.get();
    }
}
