package com.javaforge.emailservice.service;

import com.javaforge.emailservice.dto.EmailSendResponse;
import com.javaforge.emailservice.dto.SendEmailRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class EmailService {

    private final EmailSender emailSender;

    public EmailService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public EmailSendResponse send(SendEmailRequest request) {
        emailSender.send(request.to(), request.subject(), request.body());
        return new EmailSendResponse(
                "ACCEPTED",
                emailSender.deliveryMode(),
                request.to(),
                Instant.now()
        );
    }
}
