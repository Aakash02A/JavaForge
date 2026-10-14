package com.javaforge.orderservice.exception;

public class DuplicateOrderProductException extends RuntimeException {

    public DuplicateOrderProductException(long productId) {
        super("Product " + productId + " may appear only once in an order");
    }
}
