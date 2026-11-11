package com.example.apirestspringboot.exception;

public class DuplicateCategoryTitleException extends RuntimeException {
    public DuplicateCategoryTitleException(String title) {
        super("could not register category with title already exists: " + title);
    }
}
