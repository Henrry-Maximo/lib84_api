package com.example.apirestspringboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRecordDto(
        @Schema(example = "johndoe@example.com", description = "User email (must be unique)")
        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid address")
        String email,

        @Schema(example = "12345678", description = "User password (stored as a hash)")
        @NotBlank(message = "password is required")
        @Size(min = 8, max = 72, message = "password must have between 8 and 72 characters")
        String password) {
}
