package com.javaforge.bookstore.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Book(
        Long id,
        String title,
        String author,
        String isbn,
        BigDecimal price,
        int stock,
        Instant createdAt,
        Instant updatedAt
) {
}
