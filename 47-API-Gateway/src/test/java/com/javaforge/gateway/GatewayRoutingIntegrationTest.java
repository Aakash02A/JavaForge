package com.javaforge.gateway;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.nio.charset.StandardCharsets;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "eureka.client.enabled=false",
                "spring.cloud.config.enabled=false"
        }
)
class GatewayRoutingIntegrationTest {

    private static DisposableServer backend;

    @LocalServerPort
    private int gatewayPort;

    @BeforeAll
    static void startBackend() {
        backend = HttpServer.create()
                .host("127.0.0.1")
                .port(0)
                .handle((request, response) -> request.receive()
                        .aggregate()
                        .asString(StandardCharsets.UTF_8)
                        .defaultIfEmpty("")
                        .flatMap(body -> response
                                .status(200)
                                .header("X-Backend", "stub")
                                .sendString(Mono.just(body.isEmpty()
                                        ? request.method().name() + " " + request.uri()
                                        : body))
                                .then()))
                .bindNow();
    }

    @AfterAll
    static void stopBackend() {
        if (backend != null) {
            backend.disposeNow();
        }
    }

    @DynamicPropertySource
    static void configureBackend(DynamicPropertyRegistry registry) {
        registry.add("app.services.product-url", GatewayRoutingIntegrationTest::backendUrl);
        registry.add("app.services.order-url", GatewayRoutingIntegrationTest::backendUrl);
        registry.add("app.services.payment-url", GatewayRoutingIntegrationTest::backendUrl);
    }

    @Test
    void proxiesProductRouteAndPreservesQueryString() {
        client().get().uri("/api/products/12?view=full")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Backend", "stub")
                .expectBody(String.class).isEqualTo("GET /api/products/12?view=full");
    }

    @Test
    void proxiesOrderPostAndPreservesJsonBody() {
        String body = "{\"customerEmail\":\"buyer@example.com\"}";
        client().post().uri("/api/orders")
                .contentType(APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(result -> org.junit.jupiter.api.Assertions.assertEquals(
                        body,
                        new String(result.getResponseBody(), StandardCharsets.UTF_8)
                ));
    }

    @Test
    void proxiesPaymentRoute() {
        client().get().uri("/api/payments/order/42")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("GET /api/payments/order/42");
    }

    @Test
    void returnsNotFoundForUnconfiguredPath() {
        client().get().uri("/api/unknown")
                .exchange()
                .expectStatus().isNotFound();
    }

    private WebTestClient client() {
        return WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + gatewayPort)
                .build();
    }

    private static String backendUrl() {
        return "http://127.0.0.1:" + backend.port();
    }
}
