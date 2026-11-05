package com.javaforge.rolebasedaccess.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresInSeconds) {
}
