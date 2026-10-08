package com.javaforge.oauthlogin.dto;

import com.javaforge.oauthlogin.model.OAuthAccount;

import java.time.Instant;

public record ProfileResponse(Long id, String provider, String email, String displayName,
                              Instant createdAt, Instant lastLoginAt) {

    public static ProfileResponse from(OAuthAccount account) {
        return new ProfileResponse(account.getId(), account.getProvider(), account.getEmail(),
                account.getDisplayName(), account.getCreatedAt(), account.getLastLoginAt());
    }
}
