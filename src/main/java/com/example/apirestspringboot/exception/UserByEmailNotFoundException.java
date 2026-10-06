package com.example.apirestspringboot.exception;

public class UserByEmailNotFoundException extends RuntimeException {
    public UserByEmailNotFoundException(String email) {
        super("Could not find the user with E-mail: " + email);
    }
}
