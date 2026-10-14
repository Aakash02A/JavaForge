package com.javaforge.ratelimiter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.rate-limit")
public record RateLimitProperties(int requests, Duration window) {

    public RateLimitProperties {
        if (requests < 1) {
            throw new IllegalArgumentException("Rate limit requests must be greater than zero");
        }
        if (window == null || window.toMillis() < 1) {
            throw new IllegalArgumentException("Rate limit window must be at least one millisecond");
        }
    }
}
