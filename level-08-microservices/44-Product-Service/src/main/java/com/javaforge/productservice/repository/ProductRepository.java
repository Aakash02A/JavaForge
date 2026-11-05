package com.javaforge.productservice.repository;

import com.javaforge.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);
}
