package com.javaforge.libraryapi.exception;

public class LibraryNotFoundException extends RuntimeException {
    public LibraryNotFoundException(long id) {
        super("Library with id " + id + " was not found");
    }
}
