package com.javaforge.orderservice.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(long productId, int requested, int available) {
        super("Product " + productId + " has " + available + " in stock; " + requested + " requested");
    }
}
