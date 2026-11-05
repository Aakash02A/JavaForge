package com.javaforge.libraryapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler({LibraryNotFoundException.class, BookNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleBodyValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> validationErrors = new TreeMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            validationErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return error(HttpStatus.BAD_REQUEST, "Request validation failed", request, validationErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleParameterValidation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        Map<String, String> validationErrors = new TreeMap<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            String property = violation.getPropertyPath().toString();
            String field = property.substring(property.lastIndexOf('.') + 1);
            validationErrors.putIfAbsent(field, violation.getMessage());
        }
        return error(HttpStatus.BAD_REQUEST, "Request validation failed", request, validationErrors);
    }

    private ResponseEntity<ApiError> error(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> validationErrors
    ) {
        ApiError body = new ApiError(Instant.now(), status.value(), status.getReasonPhrase(),
                message, request.getRequestURI(), validationErrors);
        return ResponseEntity.status(status).body(body);
    }
}
