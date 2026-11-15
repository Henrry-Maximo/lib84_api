package com.example.apirestspringboot.dto;

import java.time.Instant;

public record InternalErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {}
