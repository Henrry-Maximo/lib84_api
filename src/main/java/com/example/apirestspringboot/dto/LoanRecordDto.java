package com.example.apirestspringboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record LoanRecordDto(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull(message = "userId is required")
        UUID userId,

        @Schema(example = "[\"3fa85f64-5717-4562-b3fc-2c963f66afa6\"]")
        @NotEmpty(message = "bookIds must contain at least one book")
        Set<@NotNull UUID> bookIds,

        @Schema(example = "2026-12-01T10:00:00")
        @NotNull(message = "loanDateScheduled is required")
        @FutureOrPresent(message = "loanDateScheduled must not be in the past")
        LocalDateTime loanDateScheduled
) {}