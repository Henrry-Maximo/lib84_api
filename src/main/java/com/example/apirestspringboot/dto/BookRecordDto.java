package com.example.apirestspringboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record BookRecordDto(
        @Schema(example = "Clean Code")
        @NotBlank(message = "title is required")
        @Size(max = 255, message = "title must have at most 255 characters")
        String title,

        @Schema(example = "9780132350884")
        @Size(max = 17, message = "isbn must have at most 17 characters")
        String isbn,

        @Schema(example = "10", description = "Number of copies in stock")
        @NotNull(message = "amount is required")
        @PositiveOrZero(message = "amount must not be negative")
        Integer amount,

        @Schema(example = "2026-08-01")
        @PastOrPresent(message = "datePublication must not be in the future")
        LocalDate datePublication,

        @NotNull(message = "categoryId is required")
        UUID categoryId,

        @NotNull(message = "supplierId is required")
        UUID supplierId,

        Set<@NotNull UUID> authorIds
) {}
