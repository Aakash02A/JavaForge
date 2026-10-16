package com.javaforge.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaforge.orderservice.client.ProductClient;
import com.javaforge.orderservice.dto.ProductResponse;
import com.javaforge.orderservice.model.CustomerOrder;
import com.javaforge.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "eureka.client.enabled=false")
@AutoConfigureMockMvc
class OrderApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @MockitoBean
    private ProductClient productClient;

    @BeforeEach
    void clearOrders() {
        orderRepository.deleteAll();
    }

    @Test
    void createsOrderUsingProductSnapshotAndCalculatesTotal() throws Exception {
        when(productClient.getProduct(10)).thenReturn(product(10, "KB-001", "Keyboard", "89.99", 10));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerEmail": "buyer@example.com",
                                  "items": [{"productId": 10, "quantity": 2}]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.items[0].productSku").value("KB-001"))
                .andExpect(jsonPath("$.items[0].unitPrice").value(89.99))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].lineTotal").value(179.98))
                .andExpect(jsonPath("$.total").value(179.98));

        verify(productClient).getProduct(10);
    }

    @Test
    void retrievesOrderAfterProductServiceChanges() throws Exception {
        when(productClient.getProduct(10)).thenReturn(product(10, "KB-001", "Keyboard", "89.99", 10));
        String response = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerEmail": "buyer@example.com",
                                  "items": [{"productId": 10, "quantity": 1}]
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        long orderId = objectMapper.readTree(response).get("id").asLong();
        when(productClient.getProduct(10)).thenThrow(new AssertionError("Order lookup must not call Product Service"));

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].productName").value("Keyboard"))
                .andExpect(jsonPath("$.total").value(89.99));
    }

    @Test
    void rejectsOrderWhenRequestedQuantityExceedsAvailableStock() throws Exception {
        when(productClient.getProduct(10)).thenReturn(product(10, "KB-001", "Keyboard", "89.99", 1));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerEmail": "buyer@example.com",
                                  "items": [{"productId": 10, "quantity": 2}]
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "Product 10 has 1 in stock; 2 requested"));
        org.junit.jupiter.api.Assertions.assertEquals(0, orderRepository.count());
    }

    @Test
    void returnsUnprocessableEntityWhenProductDoesNotExist() throws Exception {
        when(productClient.getProduct(99))
                .thenThrow(new com.javaforge.orderservice.exception.ProductNotFoundException(99));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerEmail": "buyer@example.com",
                                  "items": [{"productId": 99, "quantity": 1}]
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Product not found: 99"));
        org.junit.jupiter.api.Assertions.assertEquals(0, orderRepository.count());
    }

    @Test
    void returnsServiceUnavailableWhenProductServiceIsUnavailable() throws Exception {
        when(productClient.getProduct(10))
                .thenThrow(new com.javaforge.orderservice.exception.ProductServiceUnavailableException(
                        new IllegalStateException("Connection refused")
                ));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerEmail": "buyer@example.com",
                                  "items": [{"productId": 10, "quantity": 1}]
                                }
                                """))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("Product Service is unavailable"));
        org.junit.jupiter.api.Assertions.assertEquals(0, orderRepository.count());
    }

    @Test
    void rejectsInvalidOrderRequest() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerEmail": "not-an-email",
                                  "items": [{"productId": 0, "quantity": 0}]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.containsString("Customer email must be valid"),
                        org.hamcrest.Matchers.containsString("Product ID must be greater than zero"),
                        org.hamcrest.Matchers.containsString("Quantity must be greater than zero")
                )));
    }

    @Test
    void rejectsDuplicateProductLines() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerEmail": "buyer@example.com",
                                  "items": [
                                    {"productId": 10, "quantity": 1},
                                    {"productId": 10, "quantity": 2}
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Product 10 may appear only once in an order"));
        org.junit.jupiter.api.Assertions.assertEquals(0, orderRepository.count());
    }

    @Test
    void returnsNotFoundForUnknownOrder() throws Exception {
        mockMvc.perform(get("/api/orders/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found: 99999"));
    }

    private ProductResponse product(long id, String sku, String name, String price, int stock) {
        return new ProductResponse(
                id, sku, name, "Demo product", new BigDecimal(price), stock
        );
    }
}
