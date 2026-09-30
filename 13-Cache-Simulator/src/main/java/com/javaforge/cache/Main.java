package com.javaforge.cache;

import com.javaforge.cache.service.LRUCache;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Java Forge Cache Simulator (LRU Cache) ===");

        // Initialize LRU Cache with a capacity of 3
        int capacity = 3;
        LRUCache<Integer, String> cache = new LRUCache<>(capacity);
        System.out.println("Cache initialized with capacity: " + capacity);

        // 1. Add elements to fill the cache
        cache.put(1, "Data A");
        cache.put(2, "Data B");
        cache.put(3, "Data C");
        System.out.println("\nAfter adding 3 items:");
        System.out.println("Cache: " + cache);

        // 2. Access an element to make it the 'most recently used'
        System.out.println("\nAccessing key 1: " + cache.get(1));
        System.out.println("Cache after access (Notice '1' is moved to the end, marking it MRU):");
        System.out.println("Cache: " + cache);

        // 3. Add a new element to trigger eviction of the 'least recently used'
        System.out.println("\nAdding key 4 (Data D) - This should evict the LRU item (which is key 2):");
        cache.put(4, "Data D");
        System.out.println("Cache: " + cache);

        // 4. Edge Cases
        System.out.println("\n=== Testing Edge Cases ===");
        
        // Edge Case 1: Overwriting an existing key
        System.out.println("1. Overwriting key 3 with 'Data C-Updated':");
        cache.put(3, "Data C-Updated");
        System.out.println("Cache: " + cache);

        // Edge Case 2: Retrieving a missing key
        System.out.println("\n2. Getting evicted key 2:");
        System.out.println("Result: " + cache.get(2)); // Should be null

        // Edge Case 3: Empty Cache (Capacity 0 scenario)
        System.out.println("\n3. Testing Cache with Capacity 0:");
        LRUCache<Integer, String> zeroCache = new LRUCache<>(0);
        zeroCache.put(100, "Should Evict Instantly");
        System.out.println("Zero Capacity Cache (should be empty): " + zeroCache);

        // Edge Case 4: Null Keys and Null Values (LinkedHashMap supports them)
        System.out.println("\n4. Testing Null Keys and Null Values:");
        LRUCache<Integer, String> nullCache = new LRUCache<>(3);
        nullCache.put(null, "Null Key Value");
        nullCache.put(999, null);
        System.out.println("Null Cache: " + nullCache);
        System.out.println("Getting null key: " + nullCache.get(null));
        System.out.println("Getting key 999: " + nullCache.get(999));

        // Edge Case 5: Rapid sequential eviction
        System.out.println("\n5. Testing Rapid Eviction (capacity 2, adding 5 items):");
        LRUCache<Integer, String> rapidCache = new LRUCache<>(2);
        for (int i = 1; i <= 5; i++) {
            rapidCache.put(i, "Value-" + i);
            System.out.println("After put(" + i + "): " + rapidCache);
        }

        // Edge Case 6: Cache of size 1 (extreme minimal cache)
        System.out.println("\n6. Testing Cache with Capacity 1:");
        LRUCache<Integer, String> singleSlotCache = new LRUCache<>(1);
        singleSlotCache.put(1, "First");
        System.out.println("After put(1): " + singleSlotCache);
        singleSlotCache.put(2, "Second");
        System.out.println("After put(2) - key 1 evicted: " + singleSlotCache);
        singleSlotCache.put(1, "Back to First");
        System.out.println("After put(1) again - key 2 evicted: " + singleSlotCache);
    }
}
