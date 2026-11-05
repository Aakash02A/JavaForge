package com.javaforge.jwtauthentication.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresInSeconds) {
}
