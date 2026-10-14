package com.javaforge.ratelimiter.store;

import com.javaforge.ratelimiter.model.RateLimitWindow;

import java.time.Duration;

public interface RateLimitStore {

    RateLimitWindow increment(String clientKey, Duration window);
}
