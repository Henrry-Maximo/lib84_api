package com.example.apirestspringboot.exception;

public class SupplierByNameNotFoundException extends RuntimeException {
    public SupplierByNameNotFoundException(String name) {
        super("could not find the supplier with name: '" + name + "'.");
    }
}
