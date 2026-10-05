package com.example.apirestspringboot.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthorRecordDto(@NotBlank String name, String biography) {}
