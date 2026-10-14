package com.javaforge.rediscachedemo.controller;

import com.javaforge.rediscachedemo.dto.CacheEvictionResponse;
import com.javaforge.rediscachedemo.dto.CacheStatsResponse;
import com.javaforge.rediscachedemo.dto.ProductResponse;
import com.javaforge.rediscachedemo.dto.ProductUpdateRequest;
import com.javaforge.rediscachedemo.model.Product;
import com.javaforge.rediscachedemo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products/{id}")
    public ProductResponse getProduct(@PathVariable long id) {
        return productService.get(id);
    }

    @PutMapping("/products/{id}")
    public Product updateProduct(@PathVariable long id, @Valid @RequestBody ProductUpdateRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/cache/products/{id}")
    public CacheEvictionResponse evictProduct(@PathVariable long id) {
        return new CacheEvictionResponse(id, productService.evict(id));
    }

    @GetMapping("/cache/stats")
    @ResponseStatus(HttpStatus.OK)
    public CacheStatsResponse getCacheStats() {
        return productService.stats();
    }
}
