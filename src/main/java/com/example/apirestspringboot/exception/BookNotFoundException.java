package com.example.apirestspringboot.exception;

import java.util.UUID;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(UUID id) {
        super("could not find the book with ID: " + id);
    }
}
