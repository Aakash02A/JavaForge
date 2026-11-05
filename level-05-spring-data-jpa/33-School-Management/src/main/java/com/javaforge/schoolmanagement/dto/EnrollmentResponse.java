package com.javaforge.schoolmanagement.dto;

import com.javaforge.schoolmanagement.model.Enrollment;

import java.time.Instant;
import java.time.LocalDate;

public record EnrollmentResponse(
        Long id,
        Long studentId,
        String studentName,
        Long courseId,
        String courseCode,
        String courseTitle,
        LocalDate enrolledOn,
        Integer grade,
        long version,
        Instant createdAt
) {
    public static EnrollmentResponse from(Enrollment enrollment) {
        return new EnrollmentResponse(enrollment.getId(), enrollment.getStudent().getId(),
                enrollment.getStudent().getName(), enrollment.getCourse().getId(),
                enrollment.getCourse().getCode(), enrollment.getCourse().getTitle(),
                enrollment.getEnrolledOn(), enrollment.getGrade(), enrollment.getVersion(),
                enrollment.getCreatedAt());
    }
}
