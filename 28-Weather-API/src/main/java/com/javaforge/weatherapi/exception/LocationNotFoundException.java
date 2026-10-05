package com.javaforge.weatherapi.exception;

public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException(String location) {
        super("No weather location was found for '" + location + "'");
    }
}
