package com.javaforge.paymentservice.exception;

public class DuplicatePaymentException extends RuntimeException {

    public DuplicatePaymentException(long orderId) {
        super("A payment already exists for order " + orderId);
    }
}
