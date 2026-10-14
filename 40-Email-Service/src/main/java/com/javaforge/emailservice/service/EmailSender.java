package com.javaforge.emailservice.service;

public interface EmailSender {

    void send(String recipient, String subject, String body);

    String deliveryMode();
}
