package com.example.apirestspringboot.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.apirestspringboot.dto.v2.AuthUsersV2RecordDto;
import com.example.apirestspringboot.exception.UserSessionCredentilsInvalidException;
import com.example.apirestspringboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class AuthenticateService {

    @Value("${security.token.secret}")
    private String secretKey;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticateService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String execute(AuthUsersV2RecordDto authUsersRecordDto) {
        var user = this.userRepository.findByEmail(authUsersRecordDto.email());

        if (user.isEmpty()) {
            throw new UserSessionCredentilsInvalidException();
        }

        var passwordMatches = this.passwordEncoder.matches(authUsersRecordDto.password(), user.get().getPassword());

        if (!passwordMatches) {
            throw new UserSessionCredentilsInvalidException();
        }

        Algorithm algorithm = Algorithm.HMAC256(this.secretKey);
        // gerar o token após validações de email/password (2 horas até expirar)
        var token = JWT.create()
                .withIssuer("lib84")
                .withExpiresAt(Instant.now().plus(Duration.ofHours(2)))
                .withSubject(user.get().getId().toString()).sign(algorithm);

        return token;
    }
}
