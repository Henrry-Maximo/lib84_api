package com.example.apirestspringboot.exception;

public class AuthorsNotFoundException extends RuntimeException {
    public AuthorsNotFoundException() {
        super("author/authors not found.");
    }
}
