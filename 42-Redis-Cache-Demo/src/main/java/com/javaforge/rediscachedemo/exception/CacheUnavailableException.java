package com.javaforge.rediscachedemo.exception;

public class CacheUnavailableException extends RuntimeException {

    public CacheUnavailableException(Throwable cause) {
        super("Redis cache is unavailable", cause);
    }
}
