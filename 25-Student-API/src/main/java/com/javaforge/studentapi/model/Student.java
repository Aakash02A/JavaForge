package com.javaforge.studentapi.model;

import java.time.Instant;

public record Student(
        Long id,
        String fullName,
        String email,
        int age,
        String course,
        Instant createdAt,
        Instant updatedAt
) {
}
