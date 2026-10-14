package com.javaforge.ratelimiter.service;

import com.javaforge.ratelimiter.config.RateLimitProperties;
import com.javaforge.ratelimiter.dto.RateLimitResponse;
import com.javaforge.ratelimiter.model.RateLimitWindow;
import com.javaforge.ratelimiter.store.RateLimitStore;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

    private final RateLimitStore rateLimitStore;
    private final RateLimitProperties properties;

    public RateLimitService(RateLimitStore rateLimitStore, RateLimitProperties properties) {
        this.rateLimitStore = rateLimitStore;
        this.properties = properties;
    }

    public RateLimitResponse check(String clientKey) {
        RateLimitWindow window = rateLimitStore.increment(clientKey, properties.window());
        boolean allowed = window.requestCount() <= properties.requests();
        long remaining = Math.max(0, properties.requests() - window.requestCount());
        return new RateLimitResponse(
                allowed,
                allowed ? "Request allowed" : "Rate limit exceeded",
                properties.requests(),
                remaining,
                window.remainingWindowMillis()
        );
    }
}
