package com.javaforge.orderservice.dto;

import com.javaforge.orderservice.model.CustomerOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String customerEmail,
        String status,
        List<OrderItemResponse> items,
        BigDecimal total,
        Instant createdAt
) {

    public static OrderResponse from(CustomerOrder order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(OrderItemResponse::from)
                .toList();
        BigDecimal total = items.stream()
                .map(OrderItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new OrderResponse(
                order.getId(),
                order.getCustomerEmail(),
                order.getStatus().name(),
                items,
                total,
                order.getCreatedAt()
        );
    }
}
