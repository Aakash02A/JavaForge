package com.javaforge.paymentservice.exception;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(long id) {
        super("Payment " + id + " was not found");
    }
}
