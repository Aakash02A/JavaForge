package com.javaforge.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfiguration {

    @Bean
    public RestClient productRestClient(
            RestClient.Builder builder,
            ProductServiceProperties properties
    ) {
        return builder.baseUrl(properties.baseUrl().toString()).build();
    }
}
