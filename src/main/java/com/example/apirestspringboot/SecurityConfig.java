package com.example.apirestspringboot;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity // ativar o PreAuthorize
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    // rotas que não devem estar bloqueadas
    private static final String[] PUBLIC_INFRA = {
        "/docs", "/docs.html",
        "/swagger-ui/**", "/swagger-ui.html",
        "/api-docs/**",
        "/terms",
        "/idempotency-demo",
        "/h2-console/**",
        "/", "/*.css", "/*.js", "/*.png", "/*.webp", "/*.gif", "/*.mp4"
    };

    // manipulação da estrutura http (request) para bloquear rotas checando autenticação na v2
    @Bean
    @Order(1)
    SecurityFilterChain v2FilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(request -> "2".equals(request.getHeader("X-API-Version")))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_INFRA).permitAll()
                .requestMatchers("/session").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/users").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"authentication required.\"}" );
                })
            )
            .addFilterBefore(securityFilter, BasicAuthenticationFilter.class);
        return http.build();
    }

    // configurações da v1, desativando critérios de segurança pra deixar público
    @Bean
    @Order(2)
    SecurityFilterChain v1FilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    // método de encoder da password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
