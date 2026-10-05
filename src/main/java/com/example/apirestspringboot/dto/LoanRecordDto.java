package com.example.apirestspringboot.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record LoanRecordDto(
        @NotNull UUID userId,
        @NotNull Set<UUID> bookIds,
        LocalDateTime loanDateScheduled
) {}
