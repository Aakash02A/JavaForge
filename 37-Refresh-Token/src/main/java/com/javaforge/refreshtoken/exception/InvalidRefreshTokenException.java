package com.javaforge.refreshtoken.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Refresh token is invalid, expired, revoked, or already used");
    }
}
