package com.example.apirestspringboot.dto;

import jakarta.validation.constraints.NotBlank;

public record SupplierRecordDto(@NotBlank String name, @NotBlank String email, String phone, String cnpj) {}
