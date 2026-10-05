package com.javaforge.weatherapi.exception;

public class WeatherProviderException extends RuntimeException {
    public WeatherProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public WeatherProviderException(String message) {
        super(message);
    }
}
