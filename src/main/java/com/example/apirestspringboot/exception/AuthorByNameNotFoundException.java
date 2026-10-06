package com.example.apirestspringboot.exception;

public class AuthorByNameNotFoundException extends RuntimeException {
    public AuthorByNameNotFoundException(String name) {
        super("Could not find the author with Name: " + name);
    }
}
