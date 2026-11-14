package com.example.apirestspringboot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// spring verá as configurações e inicializará elas
@Configuration
public class SecurityConfig {

    // method para definir um objeto já existente na camada original
    @Bean // sobreescrevendo o método
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        // desabilitar spring security (default)
        http.csrf(csrf -> csrf.disable()); // cross site request forget (vulnerabilidade web)
        return http.build();
    }
    
}
