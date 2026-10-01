package com.javaforge.urlshortener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlShortenerTest {
    private UrlShortener urlShortener;

    @BeforeEach
    void setUp() {
        urlShortener = new UrlShortener();
    }

    @Test
    void testShortenAndExpand() {
        String longUrl = "https://www.example.com/some/very/long/path/to/a/resource";
        String shortUrl = urlShortener.shortenUrl(longUrl);
        
        assertNotNull(shortUrl);
        assertTrue(shortUrl.startsWith("http://short.url/"));
        
        String expandedUrl = urlShortener.expandUrl(shortUrl);
        assertEquals(longUrl, expandedUrl);
    }

    @Test
    void testShortenSameUrlTwice() {
        String longUrl = "https://www.example.com";
        String shortUrl1 = urlShortener.shortenUrl(longUrl);
        String shortUrl2 = urlShortener.shortenUrl(longUrl);
        
        assertEquals(shortUrl1, shortUrl2);
    }

    @Test
    void testExpandInvalidUrl() {
        assertThrows(IllegalArgumentException.class, () -> urlShortener.expandUrl("invalid-url"));
    }
}
