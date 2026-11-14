package com.example.apirestspringboot.exception;

    public class UserSessionCredentilsInvalid extends RuntimeException {
    public UserSessionCredentilsInvalid() {
        super("credentials invalid.");
    }
}
