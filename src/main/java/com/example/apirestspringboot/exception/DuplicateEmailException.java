package com.example.apirestspringboot.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("email '" + email + "' already exists.");
    }
}
