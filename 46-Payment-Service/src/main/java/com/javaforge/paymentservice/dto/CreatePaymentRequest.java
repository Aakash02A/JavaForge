package com.javaforge.paymentservice.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        @NotNull @Positive Long orderId,
        @NotNull @DecimalMin("0.01") @DecimalMax("9999999999.99") BigDecimal amount,
        @NotBlank @Pattern(regexp = "^[A-Za-z]{3}$", message = "must be a three-letter currency code")
        String currency,
        @NotBlank @Size(max = 40) String paymentMethod
) {
}
