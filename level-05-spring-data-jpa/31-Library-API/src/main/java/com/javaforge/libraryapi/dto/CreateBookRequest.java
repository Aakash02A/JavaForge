package com.javaforge.libraryapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBookRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,
        @NotBlank(message = "Author is required")
        @Size(max = 120, message = "Author must be at most 120 characters")
        String author,
        @NotBlank(message = "ISBN is required")
        @Size(max = 20, message = "ISBN must be at most 20 characters")
        String isbn,
        @Min(value = 1450, message = "Publication year must be 1450 or later")
        @Max(value = 2100, message = "Publication year must be 2100 or earlier")
        int publicationYear
) {
}
