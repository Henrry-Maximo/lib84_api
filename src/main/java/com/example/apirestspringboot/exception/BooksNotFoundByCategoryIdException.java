package com.example.apirestspringboot.exception;

import java.util.UUID;

public class BooksNotFoundByCategoryIdException extends RuntimeException {
    public BooksNotFoundByCategoryIdException(UUID id) {
        super("could not find the books with category ID: " + id);
    }
}
