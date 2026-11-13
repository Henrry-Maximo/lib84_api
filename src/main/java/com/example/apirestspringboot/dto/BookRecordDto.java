package com.example.apirestspringboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record BookRecordDto(
        @Schema(example = "Clean Code")
        @NotBlank(message = "title is required")
        @Size(min = 3, max = 255, message = "title must have at most 255 characters")
        String title,

        @NotBlank(message = "isbn is required")
        @Schema(example = "9780132350884")
        @Size(min = 3, max = 17, message = "isbn must have at most 17 characters")
        String isbn,

        @Schema(example = "10", description = "Number of copies in stock")
        @NotNull(message = "amount is required")
        @PositiveOrZero(message = "amount must not be negative")
        Integer amount,

        @Schema(example = "2026-08-01")
        @NotNull(message = "datePublication is required")
        @PastOrPresent(message = "datePublication must not be in the future")
        LocalDate datePublication,

        @Schema(example = "3ac59133-1c5f-4ae4-b0be-b657c68b4dc3")
        @NotNull(message = "categoryId is required")
        UUID categoryId,

        @Schema(example = "1bd59133-1c5f-4ae4-b0be-b657c68b4dc3")
        @NotNull(message = "supplierId is required")
        UUID supplierId,

        @NotEmpty(message = "authorId/authorsId is required")
        @NotEmpty Set<UUID> authorIds
) {}
