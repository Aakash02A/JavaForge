package com.javaforge.ratelimiter.exception;

public class RateLimitStoreUnavailableException extends RuntimeException {

    public RateLimitStoreUnavailableException(Throwable cause) {
        super("Rate-limit store is unavailable", cause);
    }
}
