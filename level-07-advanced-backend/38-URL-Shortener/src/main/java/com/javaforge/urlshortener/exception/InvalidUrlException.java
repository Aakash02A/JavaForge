package com.javaforge.urlshortener.exception;

public class InvalidUrlException extends RuntimeException {
    public InvalidUrlException() {
        super("URL must be an absolute HTTP or HTTPS URL");
    }
}
