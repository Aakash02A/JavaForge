package com.javaforge.monitoring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GreetingControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void returnsGreetingAndExportsPrometheusCounter() {
        ResponseEntity<GreetingResponse> greeting = restTemplate.getForEntity(
                "/api/greeting?name=Prometheus", GreetingResponse.class);

        assertThat(greeting.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(greeting.getBody()).isNotNull();
        assertThat(greeting.getBody().message()).isEqualTo("Hello, Prometheus!");

        ResponseEntity<String> metrics = restTemplate.getForEntity(
                "/actuator/prometheus", String.class);

        assertThat(metrics.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(metrics.getBody()).contains("# TYPE javaforge_greetings_total counter");
        assertThat(metrics.getBody()).containsPattern("javaforge_greetings_total\\s+[1-9][0-9]*(?:\\.[0-9]+)?");
    }

    @Test
    void exposesHealthWithoutDetails() {
        ResponseEntity<String> health = restTemplate.getForEntity("/actuator/health", String.class);

        assertThat(health.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(health.getBody()).contains("\"status\":\"UP\"");
        assertThat(health.getBody()).doesNotContain("\"components\"");
    }

    private record GreetingResponse(String message) {
    }
}
