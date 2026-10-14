package com.javaforge.emailservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendEmailRequest(
        @NotBlank(message = "Recipient is required")
        @Email(message = "Recipient must be a valid email address")
        @Size(max = 254, message = "Recipient must not exceed 254 characters")
        String to,
        @NotBlank(message = "Subject is required")
        @Size(max = 200, message = "Subject must not exceed 200 characters")
        String subject,
        @NotBlank(message = "Body is required")
        @Size(max = 10000, message = "Body must not exceed 10000 characters")
        String body
) {
}
