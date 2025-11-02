package com.connectrh.core.repository;

import com.connectrh.core.entity.PasswordResetToken;
import com.connectrh.core.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações CRUD na entidade PasswordResetToken.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {

    /**
     * Busca um token específico pelo seu valor.
     *
     * @param token O valor único do token.
     */
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUser(User user);

    /**
     * Método de limpeza: encontra todos os tokens que já expiraram.
     *
     * @param now O tempo atual (Instant.now()).
     * @return Lista de tokens expirados.
     */
    List<PasswordResetToken> findByExpiryDateBefore(Instant now);
}
