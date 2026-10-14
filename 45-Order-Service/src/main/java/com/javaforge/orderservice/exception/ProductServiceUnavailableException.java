package com.javaforge.orderservice.exception;

public class ProductServiceUnavailableException extends RuntimeException {

    public ProductServiceUnavailableException(Throwable cause) {
        super("Product Service is unavailable", cause);
    }
}
