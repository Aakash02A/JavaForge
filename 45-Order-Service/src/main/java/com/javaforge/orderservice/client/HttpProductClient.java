package com.javaforge.orderservice.client;

import com.javaforge.orderservice.dto.ProductResponse;
import com.javaforge.orderservice.exception.ProductNotFoundException;
import com.javaforge.orderservice.exception.ProductServiceUnavailableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class HttpProductClient implements ProductClient {

    private final RestClient restClient;

    public HttpProductClient(RestClient productRestClient) {
        this.restClient = productRestClient;
    }

    @Override
    public ProductResponse getProduct(long productId) {
        try {
            return restClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .body(ProductResponse.class);
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ProductNotFoundException(productId);
        } catch (RestClientException exception) {
            throw new ProductServiceUnavailableException(exception);
        }
    }
}
