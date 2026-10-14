package com.javaforge.notificationservice.dto;

import com.javaforge.notificationservice.model.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificationRequest(
        @NotBlank(message = "Recipient is required")
        @Size(max = 320, message = "Recipient must not exceed 320 characters")
        String recipient,
        @NotNull(message = "Channel is required")
        NotificationChannel channel,
        @NotBlank(message = "Message is required")
        @Size(max = 10000, message = "Message must not exceed 10000 characters")
        String message
) {
}
