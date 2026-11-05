package com.javaforge.ratelimiter.exception;

import com.javaforge.ratelimiter.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RateLimitStoreUnavailableException.class)
    public ResponseEntity<ApiError> handleStoreUnavailable(RateLimitStoreUnavailableException exception) {
        ApiError error = new ApiError(
                Instant.now(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }
}
