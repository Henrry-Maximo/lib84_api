package com.example.apirestspringboot.exception;

import java.util.UUID;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(UUID id) {
        super("could not find the loan with ID: " + id);
    }
}
