package com.javaforge.notificationservice.service;

import com.javaforge.notificationservice.dto.NotificationRequest;

import java.util.UUID;

public interface NotificationSender {

    void send(UUID notificationId, NotificationRequest request);
}
