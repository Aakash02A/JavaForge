package com.javaforge.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaforge.paymentservice.dto.CreatePaymentRequest;
import com.javaforge.paymentservice.dto.PaymentResponse;
import com.javaforge.paymentservice.exception.DuplicatePaymentException;
import com.javaforge.paymentservice.exception.PaymentNotFoundException;
import com.javaforge.paymentservice.messaging.PaymentCompletedEvent;
import com.javaforge.paymentservice.model.Payment;
import com.javaforge.paymentservice.model.PaymentOutboxEvent;
import com.javaforge.paymentservice.repository.PaymentOutboxRepository;
import com.javaforge.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class PaymentService {

    private static final String PAYMENT_COMPLETED_ROUTING_KEY = "payment.completed";

    private final PaymentRepository paymentRepository;
    private final PaymentOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentOutboxRepository outboxRepository,
            ObjectMapper objectMapper
    ) {
        this.paymentRepository = paymentRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PaymentResponse create(CreatePaymentRequest request) {
        if (paymentRepository.existsByOrderId(request.orderId())) {
            throw new DuplicatePaymentException(request.orderId());
        }

        Payment payment = paymentRepository.save(new Payment(
                request.orderId(),
                request.amount(),
                request.currency().toUpperCase(Locale.ROOT),
                request.paymentMethod()
        ));
        try {
            String payload = objectMapper.writeValueAsString(PaymentCompletedEvent.from(payment));
            outboxRepository.save(new PaymentOutboxEvent(PAYMENT_COMPLETED_ROUTING_KEY, payload));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not create payment event", exception);
        }
        return PaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse findById(long id) {
        return paymentRepository.findById(id)
                .map(PaymentResponse::from)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public PaymentResponse findByOrderId(long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(PaymentResponse::from)
                .orElseThrow(() -> new PaymentNotFoundException(orderId));
    }
}
