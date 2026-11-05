package com.javaforge.oauthlogin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(name = "oauth_accounts", uniqueConstraints = {
        @UniqueConstraint(name = "uk_oauth_provider_subject", columnNames = {"provider", "provider_subject"}),
        @UniqueConstraint(name = "uk_oauth_email", columnNames = "email")
})
public class OAuthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String provider;

    @Column(name = "provider_subject", nullable = false, length = 200)
    private String providerSubject;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(nullable = false, length = 120)
    private String displayName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_login_at", nullable = false)
    private Instant lastLoginAt;

    protected OAuthAccount() {
    }

    public OAuthAccount(String provider, String providerSubject, String email, String displayName) {
        this.provider = provider;
        this.providerSubject = providerSubject;
        this.email = email;
        this.displayName = displayName;
        this.createdAt = Instant.now();
        this.lastLoginAt = this.createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderSubject() {
        return providerSubject;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void updateProfile(String email, String displayName) {
        this.email = email;
        this.displayName = displayName;
        this.lastLoginAt = Instant.now();
    }
}
