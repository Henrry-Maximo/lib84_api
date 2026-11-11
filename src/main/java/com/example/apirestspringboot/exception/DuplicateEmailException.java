package com.example.apirestspringboot.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("could not register user with email already exists: " + email);
    }
}
