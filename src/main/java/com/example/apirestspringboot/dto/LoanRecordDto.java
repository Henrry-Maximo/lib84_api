package com.example.apirestspringboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record LoanRecordDto(
        @Schema(example = "3fa85f64-57...")
        @NotNull(message = "userId is required")
        UUID userId,

        @Schema(description = "IDs dos livros à serem emprestados")
        @NotEmpty(message = "bookIds must contain at least one book")
        Set<@NotNull UUID> bookIds,

        @Schema(example = "2026-12-01T10:00:00")
        @FutureOrPresent(message = "loanDateScheduled must not be in the past")
        LocalDateTime loanDateScheduled
) {}