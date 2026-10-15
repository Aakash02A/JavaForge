package com.javaforge.paymentservice.messaging;

import com.javaforge.paymentservice.model.Payment;
import com.javaforge.paymentservice.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentCompletedEvent(
        Long paymentId,
        Long orderId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        Instant occurredAt
) {
    public static PaymentCompletedEvent from(Payment payment) {
        return new PaymentCompletedEvent(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}
