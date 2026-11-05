package com.javaforge.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServiceDiscoveryIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void exposesEurekaRegistryEndpoint() {
        var response = restTemplate.getForEntity(
                "http://localhost:" + port + "/eureka/apps",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void exposesEurekaDashboard() {
        var response = restTemplate.getForEntity(
                "http://localhost:" + port + "/",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(MediaType.TEXT_HTML.isCompatibleWith(response.getHeaders().getContentType()));
    }
}
