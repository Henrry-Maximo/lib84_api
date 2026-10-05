package com.example.apirestspringboot.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDto(UUID id, String email, String role, LocalDateTime createdAt) {
}
