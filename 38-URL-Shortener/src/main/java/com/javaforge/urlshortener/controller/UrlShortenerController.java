package com.javaforge.urlshortener.controller;

import com.javaforge.urlshortener.dto.ShortenUrlRequest;
import com.javaforge.urlshortener.dto.ShortUrlResponse;
import com.javaforge.urlshortener.service.UrlShortenerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/urls")
public class UrlShortenerController {

    private final UrlShortenerService service;

    public UrlShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ShortUrlResponse> shorten(@Valid @RequestBody ShortenUrlRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.shorten(request.url()));
    }

    @GetMapping("/{code}")
    public ShortUrlResponse lookup(@PathVariable String code) {
        return service.lookup(code);
    }

}
