package com.example.apirestspringboot.exception;

import java.util.UUID;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(UUID id) {
        super("Could not find the loan with ID: " + id);
    }
}
