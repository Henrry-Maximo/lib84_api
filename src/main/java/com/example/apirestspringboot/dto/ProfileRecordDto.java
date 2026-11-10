package com.example.apirestspringboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ProfileRecordDto(
        @Schema(example = "John Doe")
        @NotBlank(message = "name is required")
        @Size(max = 150, message = "name must have at most 150 characters")
        String name,

        @Schema(example = "+55 11 91234-5678")
        @Pattern(regexp = "^\\+?[0-9()\\-\\s]{8,20}$", message = "phone has an invalid format")
        String phone,

        @Schema(example = "Rua das Flores, 123 - Sao Paulo/SP")
        @Size(max = 255, message = "address must have at most 255 characters")
        String address,

        @Schema(example = "3fa85f64-57...")
        @NotNull(message = "userId is required")
        UUID userId
) {}