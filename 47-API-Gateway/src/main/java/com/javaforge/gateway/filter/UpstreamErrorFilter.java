package com.javaforge.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaforge.gateway.dto.GatewayError;
import io.netty.channel.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

@Component
public class UpstreamErrorFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(UpstreamErrorFilter.class);

    private final ObjectMapper objectMapper;

    public UpstreamErrorFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).onErrorResume(exception -> {
            HttpStatus status = upstreamStatus(exception);
            if (status == null) {
                return Mono.error(exception);
            }
            logger.warn("Upstream request failed for {}", exchange.getRequest().getURI().getPath(), exception);
            if (exchange.getResponse().isCommitted()) {
                return Mono.error(exception);
            }
            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            byte[] body;
            try {
                body = objectMapper.writeValueAsBytes(new GatewayError(
                        Instant.now().toString(),
                        status.value(),
                        status.getReasonPhrase(),
                        status == HttpStatus.GATEWAY_TIMEOUT
                                ? "Upstream service timed out"
                                : "Upstream service is unavailable"
                ));
            } catch (JsonProcessingException serializationException) {
                return Mono.error(serializationException);
            }
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        });
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private HttpStatus upstreamStatus(Throwable exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof TimeoutException
                    || cause instanceof io.netty.handler.timeout.TimeoutException
                    || cause instanceof ConnectTimeoutException) {
                return HttpStatus.GATEWAY_TIMEOUT;
            }
            if (cause instanceof ConnectException || cause instanceof UnknownHostException) {
                return HttpStatus.BAD_GATEWAY;
            }
        }
        return null;
    }
}
