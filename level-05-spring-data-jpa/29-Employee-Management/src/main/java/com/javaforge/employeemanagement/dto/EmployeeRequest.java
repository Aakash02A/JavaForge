package com.javaforge.employeemanagement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must be at most 100 characters")
        String firstName,
        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must be at most 100 characters")
        String lastName,
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 254, message = "Email must be at most 254 characters")
        String email,
        @NotBlank(message = "Department is required")
        @Size(max = 100, message = "Department must be at most 100 characters")
        String department,
        @NotBlank(message = "Job title is required")
        @Size(max = 100, message = "Job title must be at most 100 characters")
        String jobTitle,
        @NotNull(message = "Salary is required")
        @DecimalMin(value = "0.00", inclusive = false, message = "Salary must be greater than zero")
        @Digits(integer = 8, fraction = 2, message = "Salary must have at most 8 integer and 2 decimal digits")
        BigDecimal salary,
        @NotNull(message = "Hire date is required")
        @PastOrPresent(message = "Hire date cannot be in the future")
        LocalDate hireDate
) {
}
