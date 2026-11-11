package com.example.apirestspringboot.exception;

import java.util.UUID;

public class AuthorNotFoundException extends RuntimeException {
    public AuthorNotFoundException(UUID id) {
        super("could not find the author with ID: " + id);
    }
}
