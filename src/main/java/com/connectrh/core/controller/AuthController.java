package com.connectrh.core.controller;

import com.connectrh.core.dto.request.CreateUserRequest;
import com.connectrh.core.dto.request.LoginRequest; // DTO de Request CORRETO
import com.connectrh.core.dto.response.CoreAuthResponse; // DTO de Response CORRETO
import com.connectrh.core.dto.response.UserCreateResponse;
import com.connectrh.core.service.AuthService;
import com.connectrh.core.entity.User; // Importa a Entidade User para mapeamento
import jakarta.validation.Valid; // Necessário para ativar as validações do LoginRequest
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller responsável por receber requisições de autenticação interna do BFF.
 * Este controller é acessado APENAS pelo BFF, garantindo que o path seja o endpoint interno.
 */
@RestController
// Este RequestMapping define a URL base: /api/v1/internal/auth
@RequestMapping("/api/v1/internal/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint de status/saúde para verificação de conectividade pelo BFF.
     * Mapeado para: GET /api/v1/internal/auth/status
     */
    @GetMapping("/status")
    public ResponseEntity<String> checkStatus() {
        return ResponseEntity.ok("Core Auth Service OK!");
    }

    /**
     * Realiza a validação de credenciais.
     * Mapeado para: POST /api/v1/internal/auth/login
     *
     * @param request Os dados de login (email e senha).
     * @return O DTO de resposta com detalhes do usuário se válido (200 OK), ou 401 Unauthorized.
     */
    @PostMapping("/login")
    public ResponseEntity<CoreAuthResponse> login(@Valid @RequestBody LoginRequest request) {

        Optional<User> userOptional = authService.validateCredentials(request.email(), request.password());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Mapear a Entidade User para o DTO de Resposta
            CoreAuthResponse response = new CoreAuthResponse();
            response.setUserId(user.getId());
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setPhoneNumber((user.getPhoneNumber()));

            // Converte o Set de Entidades Role para um Set de nomes de String (ex: "ADMIN")
            response.setRoles(user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toSet()));

            return ResponseEntity.ok(response);
        } else {
            // Lança 401 Unauthorized se as credenciais forem inválidas
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas fornecidas para o Core Service.");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<UserCreateResponse> registerUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            User newUser = authService.createUser(request);

            UserCreateResponse response = new UserCreateResponse(
                    newUser.getId(),
                    newUser.getName(),
                    newUser.getEmail(),
                    newUser.getPhoneNumber()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
