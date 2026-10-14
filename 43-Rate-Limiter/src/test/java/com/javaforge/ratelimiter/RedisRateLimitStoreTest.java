package com.javaforge.ratelimiter;

import com.javaforge.ratelimiter.exception.RateLimitStoreUnavailableException;
import com.javaforge.ratelimiter.store.RedisRateLimitStore;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RedisRateLimitStoreTest {

    @Test
    void incrementsAnAtomicRedisWindowAndReturnsItsTtl() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        when(redisTemplate.execute(
                org.mockito.ArgumentMatchers.<RedisScript<List>>any(),
                anyList(),
                eq("60000")
        )).thenReturn(List.of(4L, 12_345L));
        RedisRateLimitStore store = new RedisRateLimitStore(redisTemplate);

        var result = store.increment("192.0.2.5", Duration.ofMinutes(1));

        assertEquals(4, result.requestCount());
        assertEquals(12_345, result.remainingWindowMillis());
    }

    @Test
    void surfacesRedisAvailabilityFailure() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        when(redisTemplate.execute(
                org.mockito.ArgumentMatchers.<RedisScript<List>>any(),
                anyList(),
                eq("60000")
        )).thenThrow(new DataAccessResourceFailureException("Redis unavailable"));
        RedisRateLimitStore store = new RedisRateLimitStore(redisTemplate);

        assertThrows(
                RateLimitStoreUnavailableException.class,
                () -> store.increment("192.0.2.5", Duration.ofMinutes(1))
        );
    }
}
