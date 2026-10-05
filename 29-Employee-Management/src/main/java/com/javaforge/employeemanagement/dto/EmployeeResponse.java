package com.javaforge.employeemanagement.dto;

import com.javaforge.employeemanagement.model.Employee;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String department,
        String jobTitle,
        BigDecimal salary,
        LocalDate hireDate,
        Instant createdAt,
        Instant updatedAt
) {
    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(employee.getId(), employee.getFirstName(), employee.getLastName(),
                employee.getEmail(), employee.getDepartment(), employee.getJobTitle(),
                employee.getSalary(), employee.getHireDate(), employee.getCreatedAt(), employee.getUpdatedAt());
    }
}
