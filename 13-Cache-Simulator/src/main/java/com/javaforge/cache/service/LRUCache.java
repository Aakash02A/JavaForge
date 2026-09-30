package com.javaforge.cache.service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An LRU (Least Recently Used) Cache implementation extending LinkedHashMap.
 * It utilizes access-order instead of insertion-order to keep track of the most recently used entries.
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LRUCache(int capacity) {
        // initialCapacity, loadFactor, accessOrder (true means order by access, not insertion)
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        // Remove the oldest element when size exceeds the configured capacity
        return size() > capacity;
    }
}
