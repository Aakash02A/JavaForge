package com.javaforge.orderservice.client;

import com.javaforge.orderservice.dto.ProductResponse;

public interface ProductClient {

    ProductResponse getProduct(long productId);
}
