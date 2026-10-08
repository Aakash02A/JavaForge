package com.javaforge.refreshtoken.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresInSeconds,
                           String refreshToken, long refreshTokenExpiresInSeconds) {
}
