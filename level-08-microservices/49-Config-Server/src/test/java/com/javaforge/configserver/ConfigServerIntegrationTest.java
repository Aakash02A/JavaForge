package com.javaforge.configserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigServerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void servesCentralizedProductConfiguration() {
        var response = restTemplate.getForEntity(
                "http://localhost:" + port + "/product-service/default",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNullBodyContains(response.getBody(), "\"name\":\"product-service\"");
        assertNotNullBodyContains(response.getBody(), "\"server.port\":\"${SERVER_PORT:8080}\"");
    }

    @Test
    void servesDistinctConfigurationForEachRegisteredApplication() {
        for (String application : new String[]{"order-service", "payment-service", "api-gateway"}) {
            var response = restTemplate.getForEntity(
                    "http://localhost:" + port + "/" + application + "/default",
                    String.class
            );
            assertEquals(HttpStatus.OK, response.getStatusCode(), application);
            assertNotNullBodyContains(response.getBody(), "\"name\":\"" + application + "\"");
        }
    }

    @Test
    void returnsAnEmptyEnvironmentForUnknownApplicationConfiguration() {
        var response = restTemplate.getForEntity(
                "http://localhost:" + port + "/not-a-service/default",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNullBodyContains(response.getBody(), "\"name\":\"not-a-service\"");
        assertNotNullBodyContains(response.getBody(), "\"propertySources\":[]");
    }

    private void assertNotNullBodyContains(String body, String expected) {
        assertTrue(body != null && body.contains(expected), "Expected config response to contain " + expected);
    }
}
