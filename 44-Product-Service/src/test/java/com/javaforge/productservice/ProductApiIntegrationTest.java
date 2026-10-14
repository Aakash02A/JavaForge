package com.javaforge.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaforge.productservice.dto.ProductRequest;
import com.javaforge.productservice.model.Product;
import com.javaforge.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void clearProducts() {
        productRepository.deleteAll();
    }

    @Test
    void createsAndRetrievesProduct() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest("KB-001"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.sku").value("KB-001"))
                .andExpect(jsonPath("$.price").value(89.99))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Mechanical Keyboard"));
    }

    @Test
    void retrievesProductById() throws Exception {
        Product product = productRepository.saveAndFlush(new Product(
                "KB-002", "Keyboard", "Compact keyboard", new BigDecimal("70.00"), 12
        ));

        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("KB-002"))
                .andExpect(jsonPath("$.stockQuantity").value(12));
    }

    @Test
    void updatesExistingProduct() throws Exception {
        Product product = productRepository.saveAndFlush(new Product(
                "KB-003", "Keyboard", "Compact keyboard", new BigDecimal("70.00"), 12
        ));
        ProductRequest update = new ProductRequest(
                "KB-003", "Mechanical Keyboard", "Updated description", new BigDecimal("89.99"), 5
        );

        mockMvc.perform(put("/api/products/{id}", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mechanical Keyboard"))
                .andExpect(jsonPath("$.stockQuantity").value(5));
    }

    @Test
    void rejectsDuplicateSku() throws Exception {
        productRepository.saveAndFlush(new Product(
                "DUP-001", "First product", "Existing SKU", new BigDecimal("10.00"), 1
        ));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest("DUP-001"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "A product with SKU 'DUP-001' already exists"));
    }

    @Test
    void rejectsInvalidProductRequest() throws Exception {
        ProductRequest invalid = new ProductRequest(
                " ", " ", "Description", new BigDecimal("-1.00"), -1
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.containsString("SKU is required"),
                        org.hamcrest.Matchers.containsString("Name is required"),
                        org.hamcrest.Matchers.containsString("Price must be greater than zero"),
                        org.hamcrest.Matchers.containsString("Stock quantity must not be negative")
                )));
    }

    @Test
    void deletesProductAndReturnsNoContent() throws Exception {
        Product product = productRepository.saveAndFlush(new Product(
                "DEL-001", "Product to delete", "Delete test", new BigDecimal("5.00"), 2
        ));

        mockMvc.perform(delete("/api/products/{id}", product.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsNotFoundForUnknownProduct() throws Exception {
        mockMvc.perform(get("/api/products/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found: 99999"));
    }

    private ProductRequest productRequest(String sku) {
        return new ProductRequest(
                sku,
                "Mechanical Keyboard",
                "Compact wireless keyboard",
                new BigDecimal("89.99"),
                25
        );
    }
}
