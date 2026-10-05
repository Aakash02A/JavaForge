package com.javaforge.notesapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateNoteRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 120, message = "Title must be at most 120 characters")
        String title,
        @NotBlank(message = "Content is required")
        @Size(max = 10000, message = "Content must be at most 10000 characters")
        String content
) {
}
