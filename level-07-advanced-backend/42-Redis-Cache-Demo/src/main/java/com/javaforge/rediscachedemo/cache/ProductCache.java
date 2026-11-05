package com.javaforge.rediscachedemo.cache;

import com.javaforge.rediscachedemo.model.Product;

import java.util.Optional;

public interface ProductCache {

    Optional<Product> get(long productId);

    void put(Product product);

    boolean evict(long productId);
}
