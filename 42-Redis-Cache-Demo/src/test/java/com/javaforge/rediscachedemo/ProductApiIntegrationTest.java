package com.javaforge.rediscachedemo;

import com.javaforge.rediscachedemo.cache.ProductCache;
import com.javaforge.rediscachedemo.model.Product;
import com.javaforge.rediscachedemo.repository.ProductCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductCache productCache;

    @MockitoBean
    private ProductCatalog productCatalog;

    @Test
    void loadsProductOnCacheMissAndStoresIt() throws Exception {
        Product product = product();
        when(productCache.get(1)).thenReturn(Optional.empty());
        when(productCatalog.findById(1)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cacheStatus").value("MISS"))
                .andExpect(jsonPath("$.product.id").value(1))
                .andExpect(jsonPath("$.product.name").value("Mechanical Keyboard"));

        verify(productCache).put(product);
    }

    @Test
    void returnsCachedProductWithoutReadingCatalog() throws Exception {
        when(productCache.get(1)).thenReturn(Optional.of(product()));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cacheStatus").value("HIT"))
                .andExpect(jsonPath("$.product.price").value(89.99));
    }

    @Test
    void returnsNotFoundWhenProductDoesNotExist() throws Exception {
        when(productCache.get(99)).thenReturn(Optional.empty());
        when(productCatalog.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found: 99"));
    }

    @Test
    void updatesProductAndEvictsCachedValue() throws Exception {
        Product product = product();
        when(productCatalog.findById(1)).thenReturn(Optional.of(product));
        when(productCatalog.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productCache.evict(1)).thenReturn(true);

        mockMvc.perform(put("/api/products/1")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Updated Keyboard",
                                  "description": "Updated description",
                                  "price": 99.00
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Keyboard"))
                .andExpect(jsonPath("$.price").value(99.00));

        verify(productCache).evict(1);
    }

    @Test
    void rejectsInvalidProductUpdate() throws Exception {
        mockMvc.perform(put("/api/products/1")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": " ",
                                  "description": "Updated description",
                                  "price": -1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", org.hamcrest.Matchers.containsString("Name is required")))
                .andExpect(jsonPath("$.message", org.hamcrest.Matchers.containsString("Price must not be negative")));
    }

    @Test
    void manuallyEvictsCachedProduct() throws Exception {
        when(productCache.evict(1)).thenReturn(true);

        mockMvc.perform(delete("/api/cache/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.evicted").value(true));
    }

    private Product product() {
        return new Product(1, "Mechanical Keyboard", "Compact wireless keyboard", new BigDecimal("89.99"));
    }
}
