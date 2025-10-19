package com.connectrh.core.repository;

import com.connectrh.core.entity.Role;
import com.connectrh.core.entity.Role.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for CRUD operations on the Role entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a Role by its RoleName enum. Essential for initial setup and user management.
     *
     * @param name The RoleName enum (e.g., RoleName.ADMIN).
     * @return An Optional containing the Role, if found.
     */
    Optional<Role> findByName(RoleName name);
}
