package com.example.apirestspringboot.exception;

    public class UserSessionCredentilsInvalidException extends RuntimeException {
    public UserSessionCredentilsInvalidException() {
        super("credentials invalid.");
    }
}
