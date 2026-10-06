package com.javaforge.schoolmanagement.dto;

import com.javaforge.schoolmanagement.model.Student;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record StudentResponse(
        Long id,
        String name,
        String email,
        LocalDate dateOfBirth,
        List<EnrollmentResponse> enrollments,
        long version,
        Instant createdAt,
        Instant updatedAt
) {
    public static StudentResponse from(Student student) {
        return new StudentResponse(student.getId(), student.getName(), student.getEmail(),
                student.getDateOfBirth(), student.getEnrollments().stream().map(EnrollmentResponse::from).toList(),
                student.getVersion(), student.getCreatedAt(), student.getUpdatedAt());
    }
}
