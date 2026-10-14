package com.javaforge.rediscachedemo.cache;

import com.javaforge.rediscachedemo.exception.CacheUnavailableException;
import com.javaforge.rediscachedemo.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class RedisProductCache implements ProductCache {

    private static final String KEY_PREFIX = "products:";

    private final RedisTemplate<String, Product> redisTemplate;
    private final Duration ttl;

    public RedisProductCache(
            RedisTemplate<String, Product> redisTemplate,
            @Value("${app.cache.product-ttl:5m}") Duration ttl
    ) {
        this.redisTemplate = redisTemplate;
        this.ttl = ttl;
    }

    @Override
    public Optional<Product> get(long productId) {
        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(key(productId)));
        } catch (DataAccessException exception) {
            throw new CacheUnavailableException(exception);
        }
    }

    @Override
    public void put(Product product) {
        try {
            redisTemplate.opsForValue().set(key(product.id()), product, ttl);
        } catch (DataAccessException exception) {
            throw new CacheUnavailableException(exception);
        }
    }

    @Override
    public boolean evict(long productId) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key(productId)));
        } catch (DataAccessException exception) {
            throw new CacheUnavailableException(exception);
        }
    }

    private String key(long productId) {
        return KEY_PREFIX + productId;
    }
}
