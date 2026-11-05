package com.javaforge.paymentservice.dto;

import com.javaforge.paymentservice.model.Payment;
import com.javaforge.paymentservice.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        Long orderId,
        BigDecimal amount,
        String currency,
        String paymentMethod,
        PaymentStatus status,
        Instant createdAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}
