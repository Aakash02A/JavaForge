package com.javaforge.emailservice.controller;

import com.javaforge.emailservice.dto.EmailSendResponse;
import com.javaforge.emailservice.dto.SendEmailRequest;
import com.javaforge.emailservice.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public EmailSendResponse send(@Valid @RequestBody SendEmailRequest request) {
        return emailService.send(request);
    }
}
