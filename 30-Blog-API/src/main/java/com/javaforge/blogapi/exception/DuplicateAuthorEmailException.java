package com.javaforge.blogapi.exception;

public class DuplicateAuthorEmailException extends RuntimeException {
    public DuplicateAuthorEmailException(String email) {
        super("An author with email " + email + " already exists");
    }
}
