package com.javaforge.weatherapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ApiError> handleLocationNotFound(
            LocationNotFoundException exception,
            HttpServletRequest request
    ) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleInvalidParameters(
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

    @ExceptionHandler({WeatherProviderException.class, RestClientException.class})
    public ResponseEntity<ApiError> handleProviderFailure(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        return error(HttpStatus.BAD_GATEWAY, "Weather provider request failed", request, Map.of());
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
