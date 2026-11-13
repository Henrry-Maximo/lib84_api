package com.example.apirestspringboot.advice;

import com.example.apirestspringboot.dto.ErrorResponse;
import com.example.apirestspringboot.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(AuthorNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse authorNotFoundHandler(AuthorNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(AuthorByNameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse authorByNameNotFoundHandler(AuthorByNameNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(AuthorAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse authorByNameNotFoundHandler(AuthorAlreadyExistsException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse bookNotFoundHandler(BookNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(BooksNotFoundByCategoryIdException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse booksNotFoundByCategoryIdHandler(BooksNotFoundByCategoryIdException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(AuthorsNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse booksNotFoundByCategoryIdHandler(AuthorsNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(BooksNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse booksNotFoundHandler(BooksNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse categoryNotFoundHandler(CategoryNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(CategoryByNameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse categoryByNameNotFoundHandler(CategoryByNameNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(DuplicateCategoryTitleException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse duplicateCategoryTitleHandler(DuplicateCategoryTitleException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse duplicateEmailHandler(DuplicateEmailException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(LoanNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse loanNotFoundHandler(LoanNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse profileNotFoundHandler(ProfileNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(SupplierNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse supplierNotFoundHandler(SupplierNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(SupplierByNameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse supplierByNameNotFoundHandler(SupplierByNameNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse userNotFoundHandler(UserNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(UserByEmailNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse userByEmailNotFoundHandler(UserByEmailNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    // captura falhas do @Valid para cada campo inválido
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, List<String>> validationHandler(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(
                                f -> f.getDefaultMessage() != null
                                        ? f.getDefaultMessage()
                                        : "invalid value",
                                Collectors.toList()
                        )
                ));
    }

    // captura erros semânticos
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    ErrorResponse illegalArgumentHandler(IllegalArgumentException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse httpMessageNotReadableHandler(HttpMessageNotReadableException ex) {
        return new ErrorResponse("invalid JSON request body. Check the JSON syntax and field types.");
    }

    // captura falhas de validação em parâmetros dos métodos
    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, List<String>> handlerMethodValidation(
            HandlerMethodValidationException ex
    ) {
        var errors = new LinkedHashMap<String, List<String>>();

        for (var result : ex.getParameterValidationResults()) {

            var parameter = result.getMethodParameter();

            var requestParam = parameter.getParameterAnnotation(RequestParam.class);

            var field = requestParam != null && !requestParam.name().isBlank()
                    ? requestParam.name()
                    : Objects.requireNonNullElse(
                    parameter.getParameterName(),
                    "parameter"
            );

            var messages = result.getResolvableErrors().stream()
                    .map(error -> error.getDefaultMessage() != null
                            ? error.getDefaultMessage()
                            : "invalid value")
                    .toList();

            errors.computeIfAbsent(field, key -> new ArrayList<>())
                    .addAll(messages);
        }

        return errors;
    }
}
