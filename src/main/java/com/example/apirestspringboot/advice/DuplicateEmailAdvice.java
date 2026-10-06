//package com.example.apirestspringboot.advice;
//
//import com.example.apirestspringboot.exception.DuplicateEmailException;
//import com.example.apirestspringboot.exception.UserNotFoundException;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.util.Map;
//
//@RestControllerAdvice
//public class DuplicateEmailAdvice {
//
//    @ExceptionHandler(DuplicateEmailException.class)
//    @ResponseStatus(HttpStatus.CONFLICT)
//    Map<String, String> userNotFoundHandler(DuplicateEmailException ex) {
//        return Map.of("error", ex.getMessage());
//    }
//}
