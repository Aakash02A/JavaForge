package com.javaforge.urlshortener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UrlShortener {
    private final Map<String, String> shortToLongMap;
    private final Map<String, String> longToShortMap;
    private final String baseUrl;

    public UrlShortener() {
        this.shortToLongMap = new HashMap<>();
        this.longToShortMap = new HashMap<>();
        this.baseUrl = "http://short.url/";
    }

    public String shortenUrl(String longUrl) {
        if (longUrl == null || longUrl.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        if (longToShortMap.containsKey(longUrl)) {
            return baseUrl + longToShortMap.get(longUrl);
        }

        String shortCode = generateShortCode();
        shortToLongMap.put(shortCode, longUrl);
        longToShortMap.put(longUrl, shortCode);
        return baseUrl + shortCode;
    }

    public String expandUrl(String shortUrl) {
        if (shortUrl == null || !shortUrl.startsWith(baseUrl)) {
            throw new IllegalArgumentException("Invalid short URL");
        }

        String shortCode = shortUrl.substring(baseUrl.length());
        return shortToLongMap.getOrDefault(shortCode, null);
    }

    private String generateShortCode() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}
