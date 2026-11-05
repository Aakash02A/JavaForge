package com.javaforge.todoapi.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateCompletionRequest(
        @NotNull(message = "Completed is required")
        Boolean completed
) {
}
