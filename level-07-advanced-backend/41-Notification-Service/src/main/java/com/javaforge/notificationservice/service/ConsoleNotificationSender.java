package com.javaforge.notificationservice.service;

import com.javaforge.notificationservice.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConsoleNotificationSender implements NotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleNotificationSender.class);

    @Override
    public void send(UUID notificationId, NotificationRequest request) {
        logger.info("Processed notification {} over {}", notificationId, request.channel());
    }
}
