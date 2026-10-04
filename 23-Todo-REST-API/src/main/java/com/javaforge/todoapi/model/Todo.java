package com.javaforge.todoapi.model;

import java.time.Instant;

public record Todo(
        Long id,
        String title,
        String description,
        boolean completed,
        Instant createdAt,
        Instant updatedAt
) {
}
