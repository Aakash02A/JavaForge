package com.javaforge.refreshtoken.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "refresh_tokens",
        uniqueConstraints = @UniqueConstraint(name = "uk_refresh_tokens_hash", columnNames = "token_hash"))
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;
    @Column(name = "user_email", nullable = false, length = 254)
    private String userEmail;
    @Column(name = "family_id", nullable = false, length = 64)
    private String familyId;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "revoked_at")
    private Instant revokedAt;
    @Column(name = "replaced_by_hash", length = 64)
    private String replacedByHash;

    protected RefreshToken() {
    }

    public RefreshToken(String tokenHash, String userEmail, String familyId, Instant createdAt, Instant expiresAt) {
        this.tokenHash = tokenHash;
        this.userEmail = userEmail;
        this.familyId = familyId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getFamilyId() {
        return familyId;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public boolean isActive(Instant now) {
        return revokedAt == null && expiresAt.isAfter(now);
    }

    public void revoke(Instant timestamp, String replacementHash) {
        revokedAt = timestamp;
        replacedByHash = replacementHash;
    }
}
