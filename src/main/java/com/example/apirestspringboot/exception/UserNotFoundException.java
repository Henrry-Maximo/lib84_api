package com.example.apirestspringboot.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID id) {
        super("could not find the user with ID: " + id);
    }
}
