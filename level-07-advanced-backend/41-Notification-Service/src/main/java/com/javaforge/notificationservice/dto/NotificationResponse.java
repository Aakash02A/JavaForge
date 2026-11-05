package com.javaforge.notificationservice.dto;

import com.javaforge.notificationservice.model.NotificationChannel;
import com.javaforge.notificationservice.model.NotificationStatus;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        NotificationChannel channel,
        NotificationStatus status,
        Instant submittedAt,
        Instant updatedAt,
        String statusUrl,
        String failureReason
) {
}
