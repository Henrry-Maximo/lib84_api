package com.example.apirestspringboot.exception;

public class AuthorByNameNotFoundException extends RuntimeException {
    public AuthorByNameNotFoundException(String name) {
        super("could not find the author with name: " + name);
    }
}
