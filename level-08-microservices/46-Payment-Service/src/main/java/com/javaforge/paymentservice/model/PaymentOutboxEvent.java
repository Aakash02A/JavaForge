package com.javaforge.paymentservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "payment_outbox")
public class PaymentOutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String routingKey;

    @Column(nullable = false, columnDefinition = "text")
    private String payload;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant publishedAt;

    protected PaymentOutboxEvent() {
    }

    public PaymentOutboxEvent(String routingKey, String payload) {
        this.routingKey = routingKey;
        this.payload = payload;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void markPublished() {
        this.publishedAt = Instant.now();
    }
}
