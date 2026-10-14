package com.javaforge.rediscachedemo.dto;

public record CacheStatsResponse(long hits, long misses, long evictions) {
}
