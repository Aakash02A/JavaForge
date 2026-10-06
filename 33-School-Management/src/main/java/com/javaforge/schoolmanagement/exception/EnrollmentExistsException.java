package com.javaforge.schoolmanagement.exception;

public class EnrollmentExistsException extends RuntimeException {
    public EnrollmentExistsException(long studentId, long courseId) {
        super("Student " + studentId + " is already enrolled in course " + courseId);
    }
}
