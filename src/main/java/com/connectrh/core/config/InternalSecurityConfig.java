package com.connectrh.core.config;

import com.connectrh.core.security.ApiKeyAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Configuração de segurança para a API Interna (Core Service).
 * Garante que apenas o BFF, com a chave secreta, possa acessar os endpoints internos.
 */
@Configuration
@EnableWebSecurity
public class InternalSecurityConfig {

    @Value("${connectrh.security.internal-api-key}")
    private String internalApiKey;

    /**
     * Define o filtro customizado de autenticação por API Key.
     * Este bean é injetado na cadeia de filtros de segurança interna.
     */
    @Bean
    public ApiKeyAuthFilter apiKeyAuthFilter() {
        // O filtro lê o valor da chave secreta através do @Value
        return new ApiKeyAuthFilter();
    }

    /**
     * Configura a cadeia de filtros de segurança ESPECÍFICA para a API Interna.
     *
     * @param http Configuração de segurança HTTP.
     * @return O SecurityFilterChain configurado.
     * @throws Exception Se houver erro na configuração.
     */
    @Bean
    @Order(1) // Garante que esta configuração seja processada primeiro para URLs internas
    public SecurityFilterChain internalApiSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                // Aplica esta cadeia de filtros SOMENTE para requisições internas
                .securityMatcher(new AntPathRequestMatcher("/api/v1/internal/**"))

                // 1. Configurações básicas
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF para APIs
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 2. Adiciona o filtro customizado
                // O filtro deve rodar ANTES do filtro de Username/Password para autenticar a chave.
                .addFilterBefore(apiKeyAuthFilter(), UsernamePasswordAuthenticationFilter.class)

                // 3. Regras de autorização
                .authorizeHttpRequests(auth -> auth
                        // Exige a role ROLE_INTERNAL (atribuída pelo ApiKeyAuthFilter)
                        .requestMatchers("/api/v1/internal/**").hasRole("INTERNAL")
                );

        return http.build();
    }

    /**
     * Configuração de segurança padrão (fallback) para outros endpoints públicos.
     * Permite o acesso a qualquer outra URL que não seja interna.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain publicApiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Permite acesso a qualquer outra URL por enquanto
                );

        return http.build();
    }
}
