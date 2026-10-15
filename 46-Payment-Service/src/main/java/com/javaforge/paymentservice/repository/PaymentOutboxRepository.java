package com.javaforge.paymentservice.repository;

import com.javaforge.paymentservice.model.PaymentOutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentOutboxRepository extends JpaRepository<PaymentOutboxEvent, Long> {
    List<PaymentOutboxEvent> findTop50ByPublishedAtIsNullOrderByIdAsc();
}
