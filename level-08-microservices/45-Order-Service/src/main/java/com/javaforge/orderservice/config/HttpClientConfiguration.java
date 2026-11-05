package com.javaforge.orderservice.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfiguration {

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    @ConditionalOnProperty(
            name = "product-service.discovery-enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public RestClient productRestClient(
            @LoadBalanced RestClient.Builder builder,
            ProductServiceProperties properties
    ) {
        return builder.baseUrl(properties.baseUrl().toString()).build();
    }

    @Bean
    @ConditionalOnProperty(name = "product-service.discovery-enabled", havingValue = "false")
    public RestClient directProductRestClient(
            ProductServiceProperties properties
    ) {
        return RestClient.builder().baseUrl(properties.baseUrl().toString()).build();
    }
}
