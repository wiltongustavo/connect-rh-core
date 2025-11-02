package com.connectrh.core.controller;

import com.connectrh.core.dto.request.PasswordResetDTO;
import com.connectrh.core.dto.request.PasswordResetTokenRequestDTO;
import com.connectrh.core.dto.response.PasswordResetResponseDTO;
import com.connectrh.core.dto.response.PasswordTokenResetResponseDTO;
import com.connectrh.core.service.PasswordResetService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/auth/password")
public class InternalPasswordResetController {

    private final PasswordResetService passwordResetService;

    public InternalPasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/request")
    public ResponseEntity<PasswordTokenResetResponseDTO> requestPasswordReset(
            @Valid @RequestBody PasswordResetTokenRequestDTO request) {

        // Chama o service que já retorna o DTO
        PasswordTokenResetResponseDTO response = passwordResetService.generateResetToken(request);

        // Retorna o DTO diretamente
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset")
    public ResponseEntity<PasswordResetResponseDTO> resetPassword(@Valid @RequestBody PasswordResetDTO reset) {

        PasswordResetResponseDTO result = passwordResetService.resetPassword(reset);

        if (result.getEmail() != null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

}
