package com.example.apirestspringboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Locale;

public record AuthorRecordDto(
        @Schema(example = "George Orwell") @NotBlank @Size(min = 2, max = 155, message = "name must have between 2 and 155 characters") String name,
        @Schema(example = "romancista, jornalista, ensaísta e crítico inglês, amplamente reconhecido como uma das vozes mais influentes da literatura do século XX.") @Size(max = 255, message = "biography must have at most 255 characters") String biography)
{
    public AuthorRecordDto {
        if (name != null) {
            name = name.trim().toLowerCase(Locale.ROOT);
        }
    }
}
