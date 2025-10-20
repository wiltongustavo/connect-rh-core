package com.connectrh.core.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object para requisição de login.
 * Utilizado pelo AuthController do Core Service para receber as credenciais.
 * <p>
 * NOTE: Usamos 'senha' para corresponder à chamada no Core Controller.
 */
public record LoginRequest(
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Formato de email inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String password
) {
}
