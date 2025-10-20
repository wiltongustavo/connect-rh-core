package com.connectrh.core.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * Data Transfer Object (DTO) que o Core Service retorna ao BFF
 * após a validação bem-sucedida das credenciais de login.
 * <p>
 * Contém os dados essenciais para o BFF gerar o JWT.
 */
@Data
@NoArgsConstructor
// Corrigido: O nome da classe deve ser CoreAuthResponse
public class CoreAuthResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String email;
    private Set<String> roles; // Nomes dos perfis (e.g., "ADMIN", "EMPLOYEE")
}
