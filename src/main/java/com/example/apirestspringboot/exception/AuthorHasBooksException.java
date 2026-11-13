package com.example.apirestspringboot.exception;

import java.util.UUID;

public class AuthorHasBooksException extends RuntimeException {
    public AuthorHasBooksException(UUID authorId) {
        super("author with ID: " + authorId + " cannot be deleted because it is associated with one or more books.");
    }
}
