package com.javaforge.rediscachedemo.dto;

public record CacheEvictionResponse(long productId, boolean evicted) {
}
