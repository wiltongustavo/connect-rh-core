package com.connectrh.core.service;

import com.connectrh.core.dto.request.PasswordResetDTO;
import com.connectrh.core.dto.request.PasswordResetTokenRequestDTO;
import com.connectrh.core.dto.response.PasswordResetResponseDTO;
import com.connectrh.core.dto.response.PasswordTokenResetResponseDTO;
import com.connectrh.core.entity.PasswordResetToken;
import com.connectrh.core.entity.User;
import com.connectrh.core.repository.PasswordResetTokenRepository;
import com.connectrh.core.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private static final long EXPIRATION_MINUTES = 5;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    /**
     * Gera e salva um token de reset de senha.
     */
    public PasswordTokenResetResponseDTO generateResetToken(PasswordResetTokenRequestDTO request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return new PasswordTokenResetResponseDTO("NO_USER_FOUND");
        }

        User user = userOptional.get();
        String tokenValue = UUID.randomUUID().toString();
        Instant expiryTime = Instant.now().plusSeconds(EXPIRATION_MINUTES * 60);

        // Remove token antigo, se existir
        passwordResetTokenRepository.findByUser(user)
                .ifPresent(passwordResetTokenRepository::delete);

        // Cria e salva novo token
        PasswordResetToken token = new PasswordResetToken(tokenValue, user, expiryTime);
        passwordResetTokenRepository.save(token);

        System.out.println("DEBUG CORE: Novo token gerado para usuário " + user.getEmail() + ": " + tokenValue);
        return new PasswordTokenResetResponseDTO(tokenValue);
    }

    /**
     * Conclui o processo de redefinição de senha.
     */
    public PasswordResetResponseDTO resetPassword(PasswordResetDTO request) {
        Optional<PasswordResetToken> tokenOptional = passwordResetTokenRepository.findByToken(request.getToken());

        if (tokenOptional.isEmpty()) {
            System.out.println("DEBUG CORE: Token de reset inválido: " + request.getToken());
            return new PasswordResetResponseDTO(null, "Token de reset de senha inválido ou expirado");
        }

        PasswordResetToken resetToken = tokenOptional.get();

        if (Instant.now().isAfter(resetToken.getExpiryDate())) {
            passwordResetTokenRepository.delete(resetToken);
            System.out.println("DEBUG CORE: Token expirado: " + request.getToken());
            return new PasswordResetResponseDTO(null, "Token expirado ou inválido");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Remove o token usado
        passwordResetTokenRepository.delete(resetToken);

        System.out.println("DEBUG CORE: Senha redefinida para usuário " + user.getEmail());
        return new PasswordResetResponseDTO(user.getEmail(), "Senha resetada com sucesso");
    }
}
