package com.javaforge.urlshortener.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(name = "short_urls",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_short_urls_code", columnNames = "short_code"),
                @UniqueConstraint(name = "uk_short_urls_original", columnNames = "original_url")
        })
public class ShortUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    @Column(name = "short_code", nullable = false, length = 16)
    private String shortCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "click_count", nullable = false)
    private long clickCount;

    protected ShortUrl() {
    }

    public ShortUrl(String originalUrl, String shortCode) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.createdAt = Instant.now();
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getClickCount() {
        return clickCount;
    }

    public void recordClick() {
        clickCount++;
    }
}
