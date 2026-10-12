package com.javaforge.urlshortener.dto;

import com.javaforge.urlshortener.model.ShortUrl;

import java.time.Instant;

public record ShortUrlResponse(String shortUrl, String originalUrl, Instant createdAt, long clickCount) {

    public static ShortUrlResponse from(ShortUrl shortUrl, String publicBaseUrl) {
        return new ShortUrlResponse(
                publicBaseUrl + "/" + shortUrl.getShortCode(),
                shortUrl.getOriginalUrl(),
                shortUrl.getCreatedAt(),
                shortUrl.getClickCount());
    }
}
