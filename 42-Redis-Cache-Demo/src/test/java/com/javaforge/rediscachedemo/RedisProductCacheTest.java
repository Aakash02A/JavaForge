package com.javaforge.rediscachedemo;

import com.javaforge.rediscachedemo.cache.RedisProductCache;
import com.javaforge.rediscachedemo.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisProductCacheTest {

    @Mock
    private RedisTemplate<String, Product> redisTemplate;

    @Mock
    private ValueOperations<String, Product> valueOperations;

    private RedisProductCache productCache;

    @BeforeEach
    void setUp() {
        productCache = new RedisProductCache(redisTemplate, Duration.ofMinutes(5));
    }

    @Test
    void readsProductFromNamespacedRedisKey() {
        Product product = product();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("products:1")).thenReturn(product);

        assertEquals(Optional.of(product), productCache.get(1));
    }

    @Test
    void storesProductWithConfiguredExpiry() {
        Product product = product();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        productCache.put(product);

        verify(valueOperations).set("products:1", product, Duration.ofMinutes(5));
    }

    @Test
    void evictsNamespacedRedisKey() {
        when(redisTemplate.delete("products:1")).thenReturn(true);

        assertTrue(productCache.evict(1));
    }

    private Product product() {
        return new Product(1, "Mechanical Keyboard", "Compact wireless keyboard", new BigDecimal("89.99"));
    }
}
