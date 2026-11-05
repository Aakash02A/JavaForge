package com.javaforge.monitoring.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {
    private final Counter greetings;

    public GreetingController(MeterRegistry meterRegistry) {
        greetings = Counter.builder("javaforge.greetings")
                .description("Number of greeting requests handled")
                .register(meterRegistry);
    }

    @GetMapping("/api/greeting")
    public Greeting greeting(@RequestParam(defaultValue = "world") String name) {
        greetings.increment();
        return new Greeting("Hello, " + name + "!");
    }

    public record Greeting(String message) {
    }
}
