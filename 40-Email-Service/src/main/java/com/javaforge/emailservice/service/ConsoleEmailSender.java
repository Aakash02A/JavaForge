package com.javaforge.emailservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.mail.mode", havingValue = "console", matchIfMissing = true)
public class ConsoleEmailSender implements EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleEmailSender.class);

    @Override
    public void send(String recipient, String subject, String body) {
        logger.info("Console email accepted for recipient={} (subjectLength={}, bodyLength={})",
                recipient, subject.length(), body.length());
    }

    @Override
    public String deliveryMode() {
        return "console";
    }
}
