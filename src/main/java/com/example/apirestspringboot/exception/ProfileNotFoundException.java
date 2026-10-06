package com.example.apirestspringboot.exception;

import java.util.UUID;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(UUID id) {
        super("Could not find the profile with ID: " + id);
    }
}
