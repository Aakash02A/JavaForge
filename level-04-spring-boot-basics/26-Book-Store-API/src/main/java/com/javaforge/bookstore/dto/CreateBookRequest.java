package com.javaforge.bookstore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

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
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than zero")
        @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer and 2 decimal digits")
        BigDecimal price,
        @PositiveOrZero(message = "Stock cannot be negative")
        int stock
) {
}
