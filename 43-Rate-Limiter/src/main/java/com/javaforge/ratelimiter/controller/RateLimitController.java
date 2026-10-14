package com.javaforge.ratelimiter.controller;

import com.javaforge.ratelimiter.dto.RateLimitResponse;
import com.javaforge.ratelimiter.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RateLimitController {

    private final RateLimitService rateLimitService;

    public RateLimitController(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @GetMapping("/limited")
    public ResponseEntity<RateLimitResponse> limited(HttpServletRequest request) {
        RateLimitResponse result = rateLimitService.check(request.getRemoteAddr());
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RateLimit-Limit", Long.toString(result.limit()));
        headers.set("X-RateLimit-Remaining", Long.toString(result.remaining()));
        headers.set("X-RateLimit-Reset-Ms", Long.toString(result.resetAfterMillis()));

        if (!result.allowed()) {
            long retryAfterSeconds = Math.max(1, (result.resetAfterMillis() + 999) / 1000);
            headers.set(HttpHeaders.RETRY_AFTER, Long.toString(retryAfterSeconds));
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).headers(headers).body(result);
        }
        return ResponseEntity.ok().headers(headers).body(result);
    }
}
