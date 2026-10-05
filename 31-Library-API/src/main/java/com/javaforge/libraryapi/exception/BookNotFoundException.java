package com.javaforge.libraryapi.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(long id, long libraryId) {
        super("Book with id " + id + " was not found in library " + libraryId);
    }
}
