package com.javaforge.paymentservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaforge.paymentservice.dto.CreatePaymentRequest;
import com.javaforge.paymentservice.messaging.PaymentEventPublisher;
import com.javaforge.paymentservice.model.PaymentOutboxEvent;
import com.javaforge.paymentservice.repository.PaymentOutboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.outbox.scheduler.enabled=false")
@AutoConfigureMockMvc
class PaymentApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentOutboxRepository outboxRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentEventPublisher paymentEventPublisher;

    @Test
    void createsPaymentAndPersistsItsEventForDelivery() throws Exception {
        CreatePaymentRequest request = new CreatePaymentRequest(
                42L,
                new BigDecimal("129.50"),
                "usd",
                "card"
        );

        mockMvc.perform(post("/api/payments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(42))
                .andExpect(jsonPath("$.amount").value(129.50))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        PaymentOutboxEvent event = outboxRepository.findTop50ByPublishedAtIsNullOrderByIdAsc().getFirst();
        assertEquals("payment.completed", event.getRoutingKey());
        assertEquals(42, objectMapper.readTree(event.getPayload()).get("orderId").asLong());
        verify(paymentEventPublisher, org.mockito.Mockito.never()).publishPendingEvents();
    }

    @Test
    void rejectsASecondPaymentForTheSameOrder() throws Exception {
        CreatePaymentRequest request = new CreatePaymentRequest(43L, new BigDecimal("25.00"), "USD", "card");
        String body = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/payments").contentType("application/json").content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/payments").contentType("application/json").content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("already exists")));
    }

    @Test
    void validatesPaymentRequestFields() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .contentType("application/json")
                        .content("""
                                {"orderId":0,"amount":-1,"currency":"US","paymentMethod":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("orderId")));
    }

    @Test
    void retrievesPaymentByOrderIdAndReturnsNotFoundForUnknownOrder() throws Exception {
        CreatePaymentRequest request = new CreatePaymentRequest(44L, new BigDecimal("25.00"), "USD", "card");
        String response = mockMvc.perform(post("/api/payments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long paymentId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/payments/order/44"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentId));

        mockMvc.perform(get("/api/payments/order/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("was not found")));
    }
}
