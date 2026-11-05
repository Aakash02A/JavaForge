package com.javaforge.ratelimiter.store;

import com.javaforge.ratelimiter.exception.RateLimitStoreUnavailableException;
import com.javaforge.ratelimiter.model.RateLimitWindow;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class RedisRateLimitStore implements RateLimitStore {

    private static final DefaultRedisScript<List> INCREMENT_SCRIPT = new DefaultRedisScript<>("""
            local count = redis.call('INCR', KEYS[1])
            if count == 1 then
                redis.call('PEXPIRE', KEYS[1], ARGV[1])
            end
            return {count, redis.call('PTTL', KEYS[1])}
            """, List.class);

    private final StringRedisTemplate redisTemplate;

    public RedisRateLimitStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public RateLimitWindow increment(String clientKey, Duration window) {
        try {
            List<?> result = redisTemplate.execute(
                    INCREMENT_SCRIPT,
                    List.of(key(clientKey)),
                    Long.toString(window.toMillis())
            );
            if (result == null || result.size() != 2
                    || !(result.get(0) instanceof Number count)
                    || !(result.get(1) instanceof Number remainingWindow)) {
                throw new IllegalStateException("Redis returned an invalid rate-limit result");
            }
            return new RateLimitWindow(count.longValue(), Math.max(0, remainingWindow.longValue()));
        } catch (DataAccessException exception) {
            throw new RateLimitStoreUnavailableException(exception);
        }
    }

    private String key(String clientKey) {
        return "rate-limit:" + clientKey;
    }
}
