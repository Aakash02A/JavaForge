package com.javaforge.blogapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @NotBlank(message = "Commenter name is required")
        @Size(max = 100, message = "Commenter name must be at most 100 characters")
        String commenterName,
        @NotBlank(message = "Content is required")
        @Size(max = 4000, message = "Content must be at most 4000 characters")
        String content
) {
}
