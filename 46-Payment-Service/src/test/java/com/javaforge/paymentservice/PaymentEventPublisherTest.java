package com.javaforge.paymentservice;

import com.javaforge.paymentservice.messaging.PaymentEventPublisher;
import com.javaforge.paymentservice.model.PaymentOutboxEvent;
import com.javaforge.paymentservice.repository.PaymentOutboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentEventPublisherTest {

    @Test
    void marksAnEventPublishedAfterRabbitMqAcceptsIt() {
        PaymentOutboxRepository repository = mock(PaymentOutboxRepository.class);
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        PaymentOutboxEvent event = new PaymentOutboxEvent(
                "payment.completed",
                "{\"paymentId\":1,\"orderId\":7}"
        );
        when(repository.findTop50ByPublishedAtIsNullOrderByIdAsc()).thenReturn(List.of(event));
        PaymentEventPublisher publisher = new PaymentEventPublisher(
                repository,
                rabbitTemplate,
                new com.fasterxml.jackson.databind.ObjectMapper(),
                "payment.events"
        );

        publisher.publishPendingEvents();

        assertNotNull(event.getPublishedAt());
        verify(rabbitTemplate).convertAndSend(eq("payment.events"), eq("payment.completed"), any(Object.class));
        verify(repository).save(event);
    }

    @Test
    void leavesEventPendingWhenRabbitMqIsUnavailable() {
        PaymentOutboxRepository repository = mock(PaymentOutboxRepository.class);
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        PaymentOutboxEvent event = new PaymentOutboxEvent(
                "payment.completed",
                "{\"paymentId\":1,\"orderId\":7}"
        );
        when(repository.findTop50ByPublishedAtIsNullOrderByIdAsc()).thenReturn(List.of(event));
        org.mockito.Mockito.doThrow(new AmqpException("broker unavailable"))
                .when(rabbitTemplate).convertAndSend(eq("payment.events"), eq("payment.completed"), any(Object.class));
        PaymentEventPublisher publisher = new PaymentEventPublisher(
                repository,
                rabbitTemplate,
                new com.fasterxml.jackson.databind.ObjectMapper(),
                "payment.events"
        );

        publisher.publishPendingEvents();

        org.junit.jupiter.api.Assertions.assertNull(event.getPublishedAt());
        verify(repository, never()).save(event);
    }
}
