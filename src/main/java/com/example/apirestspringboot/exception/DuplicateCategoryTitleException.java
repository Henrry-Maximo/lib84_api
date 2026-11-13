package com.example.apirestspringboot.exception;

public class DuplicateCategoryTitleException extends RuntimeException {
    public DuplicateCategoryTitleException(String title) {
        super("category '" + title + "' already exists.");
    }
}
