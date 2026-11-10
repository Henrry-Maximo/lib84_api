package com.example.apirestspringboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserPatchDto(
        @Schema(example = "johndoe@example.com")
        @Email(message = "email must be a valid address")
        String email,

        @Schema(description = "New password (min. 8 characters)")
        @Size(min = 8, max = 72, message = "password must have between 8 and 72 characters")
        String password,

        @Schema(example = "MEMBER", allowableValues = {"MEMBER", "ADMIN"})
        @Pattern(regexp = "^(MEMBER|ADMIN)$", message = "role must be MEMBER or ADMIN")
        String role
) {}