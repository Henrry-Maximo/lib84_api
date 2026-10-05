package com.example.apirestspringboot.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRecordDto(@NotBlank String title, String description) {}
