package com.javaforge.orderservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "product-service")
public record ProductServiceProperties(URI baseUrl) {

    public ProductServiceProperties {
        if (baseUrl == null || !baseUrl.isAbsolute()) {
            throw new IllegalArgumentException("Product Service base URL must be an absolute URI");
        }
    }
}
