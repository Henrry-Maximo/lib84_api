package com.example.apirestspringboot.exception;

import java.util.UUID;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(UUID id) {
        super("Could not find the book with ID: " + id);
    }
}
