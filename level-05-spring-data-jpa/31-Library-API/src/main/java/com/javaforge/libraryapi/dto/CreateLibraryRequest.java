package com.javaforge.libraryapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateLibraryRequest(
        @NotBlank(message = "Library name is required")
        @Size(max = 150, message = "Library name must be at most 150 characters")
        String name,
        @NotBlank(message = "Address is required")
        @Size(max = 200, message = "Address must be at most 200 characters")
        String address
) {
}
