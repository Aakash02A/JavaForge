package com.javaforge.ratelimiter.model;

public record RateLimitWindow(long requestCount, long remainingWindowMillis) {

    public RateLimitWindow {
        if (requestCount < 1 || remainingWindowMillis < 0) {
            throw new IllegalArgumentException("Redis returned an invalid rate-limit window");
        }
    }
}
