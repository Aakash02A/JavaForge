package com.javaforge.schoolmanagement.exception;

public class DuplicateRecordException extends RuntimeException {
    public DuplicateRecordException(String field, String value) {
        super(field + " is already in use: " + value);
    }
}
