package com.javaforge.schoolmanagement.dto;

import com.javaforge.schoolmanagement.model.Course;

import java.time.Instant;
import java.util.List;

public record CourseResponse(
        Long id,
        String code,
        String title,
        int credits,
        List<EnrollmentResponse> enrollments,
        long version,
        Instant createdAt,
        Instant updatedAt
) {
    public static CourseResponse from(Course course) {
        return new CourseResponse(course.getId(), course.getCode(), course.getTitle(), course.getCredits(),
                course.getEnrollments().stream().map(EnrollmentResponse::from).toList(),
                course.getVersion(), course.getCreatedAt(), course.getUpdatedAt());
    }
}
