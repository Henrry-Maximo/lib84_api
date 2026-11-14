package com.example.apirestspringboot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final String[] PUBLIC_INFRA = {
        "/docs", "/docs.html",
        "/swagger-ui/**", "/swagger-ui.html",
        "/api-docs/**",
        "/terms",
        "/h2-console/**",
        "/", "/*.css", "/*.js", "/*.png", "/*.webp", "/*.gif", "/*.mp4"
    };

    /**
     * V1 chain — matches requests without X-API-Version header or with value "1".
     * All V1 traffic is fully permitted; Spring Security does not authenticate it.
     */
    @Bean
    @Order(1)
    SecurityFilterChain v1FilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(request -> {
                String version = request.getHeader("X-API-Version");
                return version == null || version.isBlank() || version.trim().equals("1");
            })
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    /**
     * V2 chain — matches all remaining requests (X-API-Version: 2).
     * Requires authentication except for the session endpoint and infra paths.
     */
    @Bean
    @Order(2)
    SecurityFilterChain v2FilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_INFRA).permitAll()
                .requestMatchers("/session").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
