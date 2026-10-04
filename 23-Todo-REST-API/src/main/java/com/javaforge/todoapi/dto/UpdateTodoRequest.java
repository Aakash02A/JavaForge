package com.javaforge.todoapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTodoRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 120, message = "Title must be at most 120 characters")
        String title,
        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,
        boolean completed
) {
}
