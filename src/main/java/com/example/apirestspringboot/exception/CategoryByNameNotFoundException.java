package com.example.apirestspringboot.exception;

import java.util.UUID;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(UUID id) {
        super("Could not find the category with ID: " + id);
    }
}
