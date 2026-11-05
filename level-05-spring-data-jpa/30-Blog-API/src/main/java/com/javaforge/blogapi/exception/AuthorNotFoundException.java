package com.javaforge.blogapi.exception;

public class AuthorNotFoundException extends RuntimeException {
    public AuthorNotFoundException(long id) {
        super("Author with id " + id + " was not found");
    }
}
