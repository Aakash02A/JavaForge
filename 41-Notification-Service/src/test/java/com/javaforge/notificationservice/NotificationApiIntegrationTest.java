package com.javaforge.notificationservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaforge.notificationservice.dto.NotificationRequest;
import com.javaforge.notificationservice.model.NotificationChannel;
import com.javaforge.notificationservice.model.NotificationStatus;
import com.javaforge.notificationservice.service.NotificationSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Duration;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NotificationSender notificationSender;

    @Test
    void acceptsNotificationAndProcessesItAsynchronously() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/notifications")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "recipient": "reader@example.com",
                                  "channel": "EMAIL",
                                  "message": "Welcome to Java Forge."
                                }
                                """))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("QUEUED"))
                .andExpect(jsonPath("$.channel").value("EMAIL"))
                .andExpect(jsonPath("$.statusUrl").isNotEmpty())
                .andExpect(jsonPath("$.recipient").doesNotExist())
                .andExpect(jsonPath("$.message").doesNotExist())
                .andReturn();

        UUID id = UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asText());

        await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                verify(notificationSender).send(
                        any(UUID.class),
                        argThat(request -> request.recipient().equals("reader@example.com")
                                && request.channel() == NotificationChannel.EMAIL
                                && request.message().equals("Welcome to Java Forge."))
                ));

        await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                mockMvc.perform(get("/api/notifications/{id}", id))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("COMPLETED"))
                        .andExpect(jsonPath("$.id").value(id.toString())));
    }

    @Test
    void rejectsInvalidNotificationWithoutSubmittingIt() throws Exception {
        mockMvc.perform(post("/api/notifications")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "recipient": " ",
                                  "channel": "EMAIL",
                                  "message": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Recipient is required")))
                .andExpect(jsonPath("$.message").value(containsString("Message is required")));
    }

    @Test
    void rejectsUnsupportedChannelWithConsistentErrorResponse() throws Exception {
        mockMvc.perform(post("/api/notifications")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "recipient": "reader@example.com",
                                  "channel": "FAX",
                                  "message": "Welcome to Java Forge."
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value(
                        "Request body is malformed or contains an unsupported value"));
    }

    @Test
    void reportsNotificationProcessingFailureInStatus() throws Exception {
        doThrow(new IllegalStateException("Provider unavailable"))
                .when(notificationSender).send(any(UUID.class), any(NotificationRequest.class));

        MvcResult result = mockMvc.perform(post("/api/notifications")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "recipient": "reader@example.com",
                                  "channel": "PUSH",
                                  "message": "Your order has shipped."
                                }
                                """))
                .andExpect(status().isAccepted())
                .andReturn();
        UUID id = UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asText());

        await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                mockMvc.perform(get("/api/notifications/{id}", id))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("FAILED"))
                        .andExpect(jsonPath("$.failureReason")
                                .value("Notification could not be processed")));
    }

    @Test
    void returnsNotFoundForUnknownNotification() throws Exception {
        mockMvc.perform(get("/api/notifications/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.startsWith(
                        "Notification not found: ")));
    }
}
