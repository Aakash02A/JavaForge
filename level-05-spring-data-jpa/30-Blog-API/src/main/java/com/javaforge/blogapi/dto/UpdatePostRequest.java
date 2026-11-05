package com.javaforge.blogapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdatePostRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,
        @NotBlank(message = "Content is required")
        @Size(max = 20000, message = "Content must be at most 20000 characters")
        String content,
        @NotNull(message = "Author ID is required")
        @Positive(message = "Author ID must be positive")
        Long authorId
) {
}
