package com.javaforge.todoapi.exception;

public class TodoNotFoundException extends RuntimeException {
    public TodoNotFoundException(long id) {
        super("Todo with id " + id + " was not found");
    }
}
