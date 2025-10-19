package com.connectrh.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * Filtro customizado para autenticar requisições internas usando uma API Key Secreta.
 * Este filtro é aplicado APENAS aos endpoints internos ("/api/v1/internal/**").
 */
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${connectrh.security.internal-api-key}")
    private String apiKeyValue; // O valor secreto esperado

    // O nome do header onde a chave é enviada pelo BFF
    private final String apiKeyHeader = "X-INTERNAL-API-KEY";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestApiKey = request.getHeader(apiKeyHeader);

        // Verifica se a chave foi fornecida e se ela corresponde ao valor esperado
        if (requestApiKey != null && requestApiKey.equals(apiKeyValue)) {
            // Autenticação bem-sucedida!

            // CRÍTICO: Cria um token de autenticação com a role ROLE_INTERNAL.
            // Isso resolve o 403 Forbidden, pois o Core Service agora sabe que
            // este chamador tem permissão para acessar os endpoints internos.
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    "internal-bff", // Principal (apenas um nome de identificação)
                    null,
                    Set.of(new SimpleGrantedAuthority("ROLE_INTERNAL"))
            );

            // Define o objeto de autenticação no contexto do Spring Security
            SecurityContextHolder.getContext().setAuthentication(auth);

        }
        // Se a chave for inválida ou ausente, a requisição será rejeitada pelo
        // SecurityFilterChain posteriormente, com 401 ou 403.

        filterChain.doFilter(request, response);
    }
}
