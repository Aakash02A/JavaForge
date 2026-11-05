package com.javaforge.rediscachedemo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductUpdateRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 120, message = "Name must not exceed 120 characters")
        String name,
        @NotBlank(message = "Description is required")
        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Price must not be negative")
        BigDecimal price
) {
}
