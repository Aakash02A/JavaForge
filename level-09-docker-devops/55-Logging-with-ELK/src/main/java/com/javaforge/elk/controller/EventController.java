package com.javaforge.elk.controller;

import com.javaforge.elk.dto.EventRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public EventResponse logEvent(@Valid @RequestBody EventRequest request) {
        String eventId = UUID.randomUUID().toString();
        try (MDC.MDCCloseable ignored = MDC.putCloseable("event_id", eventId)) {
            LOGGER.info("Application event received: {}", request.message());
        }
        return new EventResponse(eventId, "accepted");
    }

    public record EventResponse(String eventId, String status) {
    }
}
