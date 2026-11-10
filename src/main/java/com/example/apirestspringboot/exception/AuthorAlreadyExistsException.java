package com.example.apirestspringboot.exception;

public class AuthorAlreadyExistsException extends RuntimeException {
    public AuthorAlreadyExistsException(String name) {
        super(String.format("author '%s' already exists.", name));
    }
}
