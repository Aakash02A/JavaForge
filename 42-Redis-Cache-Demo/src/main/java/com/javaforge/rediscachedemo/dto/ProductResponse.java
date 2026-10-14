package com.javaforge.rediscachedemo.dto;

import com.javaforge.rediscachedemo.model.Product;

public record ProductResponse(Product product, String cacheStatus) {
}
