package com.javaforge.orderservice.dto;

import java.math.BigDecimal;

public record ProductResponse(
        long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity
) {
}
