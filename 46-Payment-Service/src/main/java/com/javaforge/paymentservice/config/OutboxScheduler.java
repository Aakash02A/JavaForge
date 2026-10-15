package com.javaforge.paymentservice.config;

import com.javaforge.paymentservice.messaging.PaymentEventPublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "app.outbox.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxScheduler {

    private final PaymentEventPublisher paymentEventPublisher;

    public OutboxScheduler(PaymentEventPublisher paymentEventPublisher) {
        this.paymentEventPublisher = paymentEventPublisher;
    }

    @Scheduled(fixedDelayString = "${app.outbox.publish-interval:1000}")
    public void publishPendingEvents() {
        paymentEventPublisher.publishPendingEvents();
    }
}
