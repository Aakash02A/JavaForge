# Project Idea

Build a **Cache Simulator** that implements the LRU (Least Recently Used) cache eviction policy using Java's `LinkedHashMap`.

# Problem Statement

Applications that repeatedly fetch data from databases or APIs suffer from high latency. A cache stores frequently accessed data in memory so it can be retrieved instantly. The LRU policy ensures the cache always keeps the most recently used items and discards the least recently used ones when space runs out.

The **Cache Simulator** demonstrates how to build an efficient LRU cache from scratch using Java's built-in `LinkedHashMap` by overriding its `removeEldestEntry` method.

# Objectives

- Store key-value data in a fixed-capacity cache.
- Automatically evict the Least Recently Used entry when the cache is full.
- Retrieve data by key in O(1) time.
- Track access order (most recently accessed items are retained).
- Handle edge cases: capacity 0, capacity 1, null keys, overwriting existing keys.

# Features

- Fixed Capacity Cache
- LRU Eviction Policy (auto-evicts when full)
- O(1) Get and Put operations
- Access-Order Tracking
- Edge Case Handling

# Cache Entry Details

- Key (Generic type K)
- Value (Generic type V)

# Tech Stack

- Java (Intermediate)
- java.util.LinkedHashMap
- Generics
- Override `removeEldestEntry()`
- Access-Order LinkedHashMap (accessOrder = true)

# Suggested Class Structure

LRUCache<K, V> extends LinkedHashMap<K, V>
-------------------------------------------
capacity
removeEldestEntry()

Main
----
main()

# How It Works

LinkedHashMap has two ordering modes:
1. Insertion-order (default) — items in the order they were inserted
2. Access-order (enabled via constructor) — items re-ordered on every get/put

By using access-order mode + overriding `removeEldestEntry()`, the oldest (LRU) entry is automatically removed when the cache exceeds its capacity.

# Sample Output

Cache initialized with capacity: 3

After adding 3 items:
Cache: {1=Data A, 2=Data B, 3=Data C}

Accessing key 1: Data A
Cache after access (Notice '1' is moved to the end, marking it MRU):
Cache: {2=Data B, 3=Data C, 1=Data A}

Adding key 4 (Data D) - This should evict the LRU item (which is key 2):
Cache: {3=Data C, 1=Data A, 4=Data D}

# Learning Outcomes

By completing this project, you will learn:

- How the LRU cache eviction policy works
- Extending `LinkedHashMap` and overriding `removeEldestEntry()`
- The difference between insertion-order and access-order LinkedHashMap
- Using bounded generics `<K, V>` in custom data structures
- How caches are used in real-world systems (Redis, CPU caches, DNS)

# Project Details

Difficulty: ⭐⭐⭐☆☆ (Intermediate)

Estimated Time: 5–8 hours

Skills Gained:
- LRU Cache Design
- LinkedHashMap Internals
- Java Generics
- Data Structures
