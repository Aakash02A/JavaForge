package com.javaforge.moviereviews.model;

import java.time.Instant;

public record MovieReview(
        Long id,
        String movieTitle,
        String reviewerName,
        int rating,
        String comment,
        Instant createdAt,
        Instant updatedAt
) {
}
