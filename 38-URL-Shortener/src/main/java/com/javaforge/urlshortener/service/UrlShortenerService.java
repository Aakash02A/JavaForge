package com.javaforge.urlshortener.service;

import com.javaforge.urlshortener.dto.ShortUrlResponse;
import com.javaforge.urlshortener.exception.InvalidUrlException;
import com.javaforge.urlshortener.exception.ShortUrlNotFoundException;
import com.javaforge.urlshortener.model.ShortUrl;
import com.javaforge.urlshortener.repository.ShortUrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Optional;

@Service
public class UrlShortenerService {

    private static final char[] BASE62 =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private final ShortUrlRepository repository;
    private final String publicBaseUrl;

    public UrlShortenerService(ShortUrlRepository repository,
                               @Value("${app.public-base-url}") String publicBaseUrl) {
        this.repository = repository;
        this.publicBaseUrl = publicBaseUrl.replaceAll("/+$", "");
    }

    @Transactional
    public ShortUrlResponse shorten(String inputUrl) {
        String normalizedUrl = normalizeAndValidate(inputUrl);
        Optional<ShortUrl> existing = repository.findByOriginalUrl(normalizedUrl);
        if (existing.isPresent()) {
            return toResponse(existing.get());
        }

        String baseCode = createBaseCode(normalizedUrl);
        for (int attempt = 0; attempt < 100; attempt++) {
            String code = attempt == 0 ? baseCode : baseCode + toBase62(attempt);
            if (repository.findByShortCode(code).isEmpty()) {
                try {
                    return toResponse(repository.saveAndFlush(new ShortUrl(normalizedUrl, code)));
                } catch (DataIntegrityViolationException exception) {
                    return repository.findByOriginalUrl(normalizedUrl)
                            .map(this::toResponse)
                            .orElseThrow(() -> exception);
                }
            }
        }
        throw new IllegalStateException("Unable to generate a unique short code");
    }

    @Transactional
    public ShortUrlResponse lookup(String code) {
        ShortUrl shortUrl = repository.findByShortCode(code)
                .orElseThrow(() -> new ShortUrlNotFoundException(code));
        return toResponse(shortUrl);
    }

    @Transactional
    public String resolveAndRecordClick(String code) {
        ShortUrl shortUrl = repository.findByShortCode(code)
                .orElseThrow(() -> new ShortUrlNotFoundException(code));
        shortUrl.recordClick();
        repository.save(shortUrl);
        return shortUrl.getOriginalUrl();
    }

    private ShortUrlResponse toResponse(ShortUrl shortUrl) {
        return ShortUrlResponse.from(shortUrl, publicBaseUrl);
    }

    private String normalizeAndValidate(String inputUrl) {
        if (inputUrl == null || inputUrl.isBlank()) {
            throw new InvalidUrlException();
        }
        String normalizedUrl = inputUrl.trim();
        try {
            URI uri = new URI(normalizedUrl);
            String scheme = uri.getScheme();
            if (!uri.isAbsolute() || uri.getHost() == null
                    || (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))) {
                throw new InvalidUrlException();
            }
            return uri.toASCIIString();
        } catch (URISyntaxException exception) {
            throw new InvalidUrlException();
        }
    }

    private String createBaseCode(String url) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(url.getBytes(StandardCharsets.UTF_8));
            long value = 0;
            for (int index = 0; index < 8; index++) {
                value = (value << 8) | (digest[index] & 0xffL);
            }
            String encoded = toBase62(value & Long.MAX_VALUE);
            if (encoded.length() >= 8) {
                return encoded.substring(0, 8);
            }
            return "00000000".substring(encoded.length()) + encoded;
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private String toBase62(long value) {
        if (value == 0) {
            return "0";
        }
        StringBuilder encoded = new StringBuilder();
        while (value > 0) {
            encoded.append(BASE62[(int) (value % BASE62.length)]);
            value /= BASE62.length;
        }
        return encoded.reverse().toString();
    }
}
