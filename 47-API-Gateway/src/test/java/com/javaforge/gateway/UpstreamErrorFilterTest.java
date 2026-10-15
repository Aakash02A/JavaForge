package com.javaforge.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaforge.gateway.filter.UpstreamErrorFilter;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpstreamErrorFilterTest {

    private final UpstreamErrorFilter filter = new UpstreamErrorFilter(new ObjectMapper());

    @Test
    void mapsConnectionFailureToBadGatewayJson() {
        MockServerWebExchange exchange = exchange();
        GatewayFilterChain chain = ignored -> Mono.error(new ConnectException("connection refused"));

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.BAD_GATEWAY, exchange.getResponse().getStatusCode());
        assertTrue(responseBody(exchange).contains("\"message\":\"Upstream service is unavailable\""));
    }

    @Test
    void mapsTimeoutToGatewayTimeoutJson() {
        MockServerWebExchange exchange = exchange();
        GatewayFilterChain chain = ignored -> Mono.error(new TimeoutException("upstream timed out"));

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, exchange.getResponse().getStatusCode());
        assertTrue(responseBody(exchange).contains("\"message\":\"Upstream service timed out\""));
    }

    @Test
    void mapsNettyReadTimeoutToGatewayTimeoutJson() {
        MockServerWebExchange exchange = exchange();
        GatewayFilterChain chain = ignored -> Mono.error(
                io.netty.handler.timeout.ReadTimeoutException.INSTANCE
        );

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, exchange.getResponse().getStatusCode());
        assertTrue(responseBody(exchange).contains("\"message\":\"Upstream service timed out\""));
    }

    @Test
    void propagatesUnrelatedFailuresInsteadOfHidingThem() {
        MockServerWebExchange exchange = exchange();
        IllegalStateException failure = new IllegalStateException("unexpected filter failure");
        GatewayFilterChain chain = ignored -> Mono.error(failure);

        org.junit.jupiter.api.Assertions.assertSame(
                failure,
                org.junit.jupiter.api.Assertions.assertThrows(
                        IllegalStateException.class,
                        () -> filter.filter(exchange, chain).block()
                )
        );
    }

    private MockServerWebExchange exchange() {
        return MockServerWebExchange.from(MockServerHttpRequest.get("/api/products").build());
    }

    private String responseBody(MockServerWebExchange exchange) {
        return exchange.getResponse().getBodyAsString().block();
    }
}
