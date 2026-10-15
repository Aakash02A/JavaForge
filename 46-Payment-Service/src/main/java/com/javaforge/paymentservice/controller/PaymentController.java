package com.javaforge.paymentservice.controller;

import com.javaforge.paymentservice.dto.CreatePaymentRequest;
import com.javaforge.paymentservice.dto.PaymentResponse;
import com.javaforge.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse create(@Valid @RequestBody CreatePaymentRequest request) {
        return paymentService.create(request);
    }

    @GetMapping("/{id}")
    public PaymentResponse findById(@PathVariable long id) {
        return paymentService.findById(id);
    }

    @GetMapping("/order/{orderId}")
    public PaymentResponse findByOrderId(@PathVariable long orderId) {
        return paymentService.findByOrderId(orderId);
    }
}
