package com.example.apirestspringboot.exception;

import java.util.UUID;

public class SupplierNotFoundException extends RuntimeException {
    public SupplierNotFoundException(UUID id) {
        super("could not find the supplier with ID: " + id);
    }
}
