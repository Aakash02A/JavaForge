package com.javaforge.gateway.dto;

public record GatewayError(String timestamp, int status, String error, String message) {
}
