package com.connectrh.core.repository;

import com.connectrh.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for CRUD operations on the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a User by their email. Essential for the login logic (as username).
     *
     * @param email The user's email (username).
     * @return An Optional containing the User, if found.
     */
    Optional<User> findByEmail(String email);
}
