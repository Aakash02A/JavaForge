package com.javaforge.studentapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateStudentRequest(
        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Full name must be at most 100 characters")
        String fullName,
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 254, message = "Email must be at most 254 characters")
        String email,
        @Min(value = 16, message = "Age must be at least 16")
        @Max(value = 120, message = "Age must be at most 120")
        int age,
        @NotBlank(message = "Course is required")
        @Size(max = 100, message = "Course must be at most 100 characters")
        String course
) {
}
