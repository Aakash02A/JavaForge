package com.javaforge.notificationservice.controller;

import com.javaforge.notificationservice.dto.NotificationRequest;
import com.javaforge.notificationservice.dto.NotificationResponse;
import com.javaforge.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public NotificationResponse submit(@Valid @RequestBody NotificationRequest request) {
        return notificationService.submit(request);
    }

    @GetMapping("/{id}")
    public NotificationResponse getStatus(@PathVariable UUID id) {
        return notificationService.get(id);
    }
}
