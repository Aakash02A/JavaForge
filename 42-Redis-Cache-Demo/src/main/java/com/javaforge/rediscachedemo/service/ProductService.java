package com.javaforge.rediscachedemo.service;

import com.javaforge.rediscachedemo.cache.ProductCache;
import com.javaforge.rediscachedemo.dto.CacheStatsResponse;
import com.javaforge.rediscachedemo.dto.ProductResponse;
import com.javaforge.rediscachedemo.dto.ProductUpdateRequest;
import com.javaforge.rediscachedemo.exception.ProductNotFoundException;
import com.javaforge.rediscachedemo.model.Product;
import com.javaforge.rediscachedemo.repository.ProductCatalog;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductService {

    private final ProductCatalog productCatalog;
    private final ProductCache productCache;
    private final AtomicLong hits = new AtomicLong();
    private final AtomicLong misses = new AtomicLong();
    private final AtomicLong evictions = new AtomicLong();

    public ProductService(ProductCatalog productCatalog, ProductCache productCache) {
        this.productCatalog = productCatalog;
        this.productCache = productCache;
    }

    public ProductResponse get(long id) {
        var cached = productCache.get(id);
        if (cached.isPresent()) {
            hits.incrementAndGet();
            return new ProductResponse(cached.get(), "HIT");
        }

        misses.incrementAndGet();
        Product product = productCatalog.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        productCache.put(product);
        return new ProductResponse(product, "MISS");
    }

    public Product update(long id, ProductUpdateRequest request) {
        if (productCatalog.findById(id).isEmpty()) {
            throw new ProductNotFoundException(id);
        }

        Product updated = productCatalog.save(
                new Product(id, request.name(), request.description(), request.price())
        );
        evict(id);
        return updated;
    }

    public boolean evict(long id) {
        boolean evicted = productCache.evict(id);
        if (evicted) {
            evictions.incrementAndGet();
        }
        return evicted;
    }

    public CacheStatsResponse stats() {
        return new CacheStatsResponse(hits.get(), misses.get(), evictions.get());
    }
}
