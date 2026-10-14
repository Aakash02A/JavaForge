package com.javaforge.rediscachedemo.repository;

import com.javaforge.rediscachedemo.model.Product;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryProductCatalog implements ProductCatalog {

    private final Map<Long, Product> products = new ConcurrentHashMap<>(Map.of(
            1L, new Product(1, "Mechanical Keyboard", "Compact wireless keyboard", new BigDecimal("89.99")),
            2L, new Product(2, "USB-C Hub", "Seven-port USB-C adapter", new BigDecimal("44.50")),
            3L, new Product(3, "Desk Lamp", "Dimmable LED desk lamp", new BigDecimal("32.00"))
    ));

    @Override
    public Optional<Product> findById(long id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public Product save(Product product) {
        products.put(product.id(), product);
        return product;
    }
}
