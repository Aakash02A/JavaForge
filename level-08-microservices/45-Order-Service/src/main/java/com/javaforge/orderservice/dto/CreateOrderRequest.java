package com.javaforge.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank(message = "Customer email is required")
        @Email(message = "Customer email must be valid")
        @Size(max = 254, message = "Customer email must not exceed 254 characters")
        String customerEmail,
        @NotEmpty(message = "At least one order item is required")
        @Size(max = 50, message = "An order cannot contain more than 50 items")
        List<@Valid OrderItemRequest> items
) {
}
