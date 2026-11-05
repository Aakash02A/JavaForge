package com.javaforge.employeemanagement.exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(long id) {
        super("Employee with id " + id + " was not found");
    }
}
