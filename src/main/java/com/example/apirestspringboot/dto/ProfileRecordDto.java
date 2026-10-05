package com.example.apirestspringboot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProfileRecordDto(@NotBlank String name, String phone, String address, @NotNull UUID userId) {}
