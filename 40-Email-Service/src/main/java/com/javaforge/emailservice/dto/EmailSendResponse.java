package com.javaforge.emailservice.dto;

import java.time.Instant;

public record EmailSendResponse(
        String status,
        String deliveryMode,
        String recipient,
        Instant acceptedAt
) {
}
