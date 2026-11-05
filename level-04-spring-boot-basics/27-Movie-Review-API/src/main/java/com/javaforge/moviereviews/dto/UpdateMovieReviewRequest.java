package com.javaforge.moviereviews.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateMovieReviewRequest(
        @NotBlank(message = "Movie title is required")
        @Size(max = 200, message = "Movie title must be at most 200 characters")
        String movieTitle,
        @NotBlank(message = "Reviewer name is required")
        @Size(max = 100, message = "Reviewer name must be at most 100 characters")
        String reviewerName,
        @Min(value = 1, message = "Rating must be between 1 and 5")
        @Max(value = 5, message = "Rating must be between 1 and 5")
        int rating,
        @NotBlank(message = "Comment is required")
        @Size(max = 2000, message = "Comment must be at most 2000 characters")
        String comment
) {
}
