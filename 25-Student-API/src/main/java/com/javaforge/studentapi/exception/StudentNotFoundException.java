package com.javaforge.studentapi.exception;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(long id) {
        super("Student with id " + id + " was not found");
    }
}
