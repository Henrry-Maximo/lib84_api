package com.example.apirestspringboot.exception;

public class DuplicateCategoryTitleException extends RuntimeException {
    public DuplicateCategoryTitleException(String title) {
        super("Could not register category with title already exists: " + title);
    }
}
