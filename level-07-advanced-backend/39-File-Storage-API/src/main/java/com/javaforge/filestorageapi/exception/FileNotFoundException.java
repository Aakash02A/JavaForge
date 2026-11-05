package com.javaforge.filestorageapi.exception;

public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException(Long id) {
        super("File not found with id: " + id);
    }
}
