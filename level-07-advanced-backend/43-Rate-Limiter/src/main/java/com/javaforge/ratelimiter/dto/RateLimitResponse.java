package com.javaforge.ratelimiter.dto;

public record RateLimitResponse(
        boolean allowed,
        String message,
        long limit,
        long remaining,
        long resetAfterMillis
) {
}
