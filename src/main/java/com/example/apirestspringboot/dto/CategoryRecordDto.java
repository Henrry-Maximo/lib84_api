package com.example.apirestspringboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRecordDto(
        @Schema(example = "Engenharia de Software")
        @NotBlank(message = "title is required")
        @Size(max = 100, message = "title must have at most 100 characters")
        String title,

        @Schema(example = "Livros sobre design de software e arquitetura")
        @Size(max = 500, message = "description must have at most 500 characters")
        String description
) {}