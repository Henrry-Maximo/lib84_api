package com.example.apirestspringboot.config;

import com.example.apirestspringboot.entity.User;
import com.example.apirestspringboot.repository.UserRepository; // Ajuste para o seu repository
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder; // Se usar Spring Security

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // verifica se já existe um admin cadastrado para não duplicar ao reiniciar
            if (userRepository.findByEmail("henrry@gmail.com").isEmpty()) {
                User admin = new User();

                admin.setEmail("henrry@gmail.com");
                // criptografe a senha se sua aplicação exigir BCrypt
                admin.setPassword(passwordEncoder.encode("123456789"));
                admin.setRole(User.Role.valueOf("ADMIN")); // Garante que a role seja ADMIN

                userRepository.save(admin);
                System.out.println(">> Usuário ADMIN padrão criado com sucesso: henrry@gmail.com     / 123456789");
            }
        };
    }
}