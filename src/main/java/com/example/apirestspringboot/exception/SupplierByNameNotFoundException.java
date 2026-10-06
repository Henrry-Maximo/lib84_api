package com.example.apirestspringboot.exception;

import java.util.UUID;

public class SupplierNotFoundException extends RuntimeException {
    public SupplierNotFoundException(UUID id) {
        super("Could not find the supplier with ID: " + id);
    }
}
