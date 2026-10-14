package com.javaforge.ratelimiter;

import com.javaforge.ratelimiter.model.RateLimitWindow;
import com.javaforge.ratelimiter.store.RateLimitStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.rate-limit.requests=2",
        "app.rate-limit.window=1m"
})
@AutoConfigureMockMvc
class RateLimitApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RateLimitStore rateLimitStore;

    @Test
    void allowsRequestsBelowLimitAndReturnsRateHeaders() throws Exception {
        when(rateLimitStore.increment("192.0.2.10", Duration.ofMinutes(1)))
                .thenReturn(new RateLimitWindow(1, 60_000));

        mockMvc.perform(get("/api/limited").with(request -> {
                    request.setRemoteAddr("192.0.2.10");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allowed").value(true))
                .andExpect(jsonPath("$.remaining").value(1))
                .andExpect(header().string("X-RateLimit-Limit", "2"))
                .andExpect(header().string("X-RateLimit-Remaining", "1"));
    }

    @Test
    void rejectsRequestsOverLimitAndReturnsRetryAfter() throws Exception {
        when(rateLimitStore.increment("192.0.2.11", Duration.ofMinutes(1)))
                .thenReturn(new RateLimitWindow(3, 15_500));

        mockMvc.perform(get("/api/limited").with(request -> {
                    request.setRemoteAddr("192.0.2.11");
                    return request;
                }))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.allowed").value(false))
                .andExpect(jsonPath("$.remaining").value(0))
                .andExpect(jsonPath("$.message").value("Rate limit exceeded"))
                .andExpect(header().string("Retry-After", "16"))
                .andExpect(header().string("X-RateLimit-Reset-Ms", "15500"));
    }

    @Test
    void returnsServiceUnavailableWhenRedisStoreIsUnavailable() throws Exception {
        when(rateLimitStore.increment("192.0.2.12", Duration.ofMinutes(1)))
                .thenThrow(new com.javaforge.ratelimiter.exception.RateLimitStoreUnavailableException(
                        new IllegalStateException("Redis unavailable")
                ));

        mockMvc.perform(get("/api/limited").with(request -> {
                    request.setRemoteAddr("192.0.2.12");
                    return request;
                }))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("Rate-limit store is unavailable"));
    }
}
