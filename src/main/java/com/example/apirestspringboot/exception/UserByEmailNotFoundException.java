package com.example.apirestspringboot.exception;

public class UserByEmailNotFoundException extends RuntimeException {
    public UserByEmailNotFoundException(String email) {
        super("could not find the user with email: '" + email + "'.");
    }
}
