package com.javaforge.notesapi.model;

import java.time.Instant;

public record Note(
        Long id,
        String title,
        String content,
        Instant createdAt,
        Instant updatedAt
) {
}
