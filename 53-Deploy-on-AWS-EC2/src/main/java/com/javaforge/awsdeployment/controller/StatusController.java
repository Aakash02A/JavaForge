package com.javaforge.awsdeployment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {
    @GetMapping("/api/status")
    public ApiStatus status() {
        return new ApiStatus("running", "Hello from AWS EC2");
    }

    @GetMapping("/health")
    public HealthStatus health() {
        return new HealthStatus("UP");
    }

    public record ApiStatus(String status, String message) {
    }

    public record HealthStatus(String status) {
    }
}
