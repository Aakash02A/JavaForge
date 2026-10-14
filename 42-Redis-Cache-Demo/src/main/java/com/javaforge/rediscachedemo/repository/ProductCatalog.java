package com.javaforge.rediscachedemo.repository;

import com.javaforge.rediscachedemo.model.Product;

import java.util.Optional;

public interface ProductCatalog {

    Optional<Product> findById(long id);

    Product save(Product product);
}
