package com.javaforge.urlshortener.repository;

import com.javaforge.urlshortener.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    Optional<ShortUrl> findByOriginalUrl(String originalUrl);

    Optional<ShortUrl> findByShortCode(String shortCode);
}
