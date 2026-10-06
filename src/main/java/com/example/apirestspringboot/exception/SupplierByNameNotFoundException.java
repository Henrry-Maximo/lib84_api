package com.example.apirestspringboot.exception;

public class SupplierByNameNotFoundException extends RuntimeException {
    public SupplierByNameNotFoundException(String name) {
        super("Could not find the supplier with Name: " + name);
    }
}
