package com.example.apirestspringboot.exception;


public class CategoryByNameNotFoundException extends RuntimeException {
    public CategoryByNameNotFoundException(String title) {
        super("could not find the category with title: '" + title + "'.");
    }
}
