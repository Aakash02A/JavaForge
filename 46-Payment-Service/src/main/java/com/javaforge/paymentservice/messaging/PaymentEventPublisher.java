package com.javaforge.paymentservice.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaforge.paymentservice.model.PaymentOutboxEvent;
import com.javaforge.paymentservice.repository.PaymentOutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventPublisher.class);

    private final PaymentOutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final String exchange;

    public PaymentEventPublisher(
            PaymentOutboxRepository outboxRepository,
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper,
            @Value("${app.messaging.payment-exchange}") String exchange
    ) {
        this.outboxRepository = outboxRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.exchange = exchange;
    }

    @Transactional
    public void publishPendingEvents() {
        List<PaymentOutboxEvent> pendingEvents = outboxRepository.findTop50ByPublishedAtIsNullOrderByIdAsc();
        for (PaymentOutboxEvent event : pendingEvents) {
            try {
                Object payload = objectMapper.readTree(event.getPayload());
                rabbitTemplate.convertAndSend(exchange, event.getRoutingKey(), payload);
                event.markPublished();
                outboxRepository.save(event);
            } catch (AmqpException exception) {
                logger.warn("Could not publish payment outbox event {}; it will be retried", event.getId(), exception);
                return;
            } catch (JsonProcessingException exception) {
                throw new IllegalStateException("Stored payment outbox event is not valid JSON", exception);
            }
        }
    }
}
