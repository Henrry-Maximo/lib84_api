package com.example.apirestspringboot.exception;


public class CategoryByNameNotFoundException extends RuntimeException {
    public CategoryByNameNotFoundException(String title) {
        super("Could not find the category with Name: " + title);
    }
}
