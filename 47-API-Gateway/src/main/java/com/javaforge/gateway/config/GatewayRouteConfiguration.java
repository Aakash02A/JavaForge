package com.javaforge.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRouteConfiguration {

    @Bean
    RouteLocator serviceRoutes(
            RouteLocatorBuilder builder,
            @Value("${app.services.product-url:http://localhost:8080}") String productServiceUrl,
            @Value("${app.services.order-url:http://localhost:8081}") String orderServiceUrl,
            @Value("${app.services.payment-url:http://localhost:8082}") String paymentServiceUrl
    ) {
        return builder.routes()
                .route("products", route -> route.path("/api/products", "/api/products/**")
                        .uri(productServiceUrl))
                .route("orders", route -> route.path("/api/orders", "/api/orders/**")
                        .uri(orderServiceUrl))
                .route("payments", route -> route.path("/api/payments", "/api/payments/**")
                        .uri(paymentServiceUrl))
                .build();
    }
}
