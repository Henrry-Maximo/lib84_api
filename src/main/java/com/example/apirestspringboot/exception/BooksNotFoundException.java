package com.example.apirestspringboot.exception;

public class BooksNotFoundException extends RuntimeException {
    public BooksNotFoundException() {
        super("one or more books not found.");
    }
}
