package com.example.apirestspringboot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record BookRecordDto(
        @NotBlank String title,
        String isbn,
        Integer amount,
        LocalDate datePublication,
        @NotNull UUID categoryId,
        @NotNull UUID supplierId,
        Set<UUID> authorIds
) {}
