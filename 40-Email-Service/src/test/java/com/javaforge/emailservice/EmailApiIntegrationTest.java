package com.javaforge.emailservice;

import com.javaforge.emailservice.service.EmailSender;
import com.javaforge.emailservice.exception.EmailDeliveryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.mail.mode=smtp")
@AutoConfigureMockMvc
class EmailApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailSender emailSender;

    @BeforeEach
    void configureSenderMode() {
        when(emailSender.deliveryMode()).thenReturn("smtp");
    }

    @Test
    void acceptsValidEmailAndPassesItToSender() throws Exception {
        mockMvc.perform(post("/api/emails")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "to": "reader@example.com",
                                  "subject": "Welcome",
                                  "body": "Thanks for joining."
                                }
                                """))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.deliveryMode").value("smtp"))
                .andExpect(jsonPath("$.recipient").value("reader@example.com"))
                .andExpect(jsonPath("$.acceptedAt").isNotEmpty());

        verify(emailSender).send(
                eq("reader@example.com"),
                eq("Welcome"),
                eq("Thanks for joining.")
        );
    }

    @Test
    void rejectsInvalidRecipientWithoutCallingSender() throws Exception {
        mockMvc.perform(post("/api/emails")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "to": "not-an-email",
                                  "subject": "Welcome",
                                  "body": "Thanks for joining."
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Recipient must be a valid email address"));
    }

    @Test
    void returnsBadGatewayWhenSmtpDeliveryFails() throws Exception {
        doThrow(new EmailDeliveryException(
                "Email could not be submitted to the SMTP server",
                new IllegalStateException("SMTP unavailable")
        ))
                .when(emailSender).send("reader@example.com", "Welcome", "Thanks for joining.");

        mockMvc.perform(post("/api/emails")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "to": "reader@example.com",
                                  "subject": "Welcome",
                                  "body": "Thanks for joining."
                                }
                                """))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message")
                        .value("Email could not be submitted to the SMTP server"));
    }
}
