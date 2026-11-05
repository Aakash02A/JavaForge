package com.javaforge.productservice.service;

import com.javaforge.productservice.dto.ProductRequest;
import com.javaforge.productservice.dto.ProductResponse;
import com.javaforge.productservice.exception.DuplicateProductSkuException;
import com.javaforge.productservice.exception.ProductNotFoundException;
import com.javaforge.productservice.model.Product;
import com.javaforge.productservice.repository.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(long id) {
        return ProductResponse.from(getProduct(id));
    }

    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateProductSkuException(request.sku());
        }
        try {
            return ProductResponse.from(productRepository.saveAndFlush(new Product(
                    request.sku(),
                    request.name(),
                    request.description(),
                    request.price(),
                    request.stockQuantity()
            )));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateProductSkuException(request.sku());
        }
    }

    public ProductResponse update(long id, ProductRequest request) {
        Product product = getProduct(id);
        if (productRepository.existsBySkuAndIdNot(request.sku(), id)) {
            throw new DuplicateProductSkuException(request.sku());
        }

        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        try {
            return ProductResponse.from(productRepository.saveAndFlush(product));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateProductSkuException(request.sku());
        }
    }

    public void delete(long id) {
        Product product = getProduct(id);
        productRepository.delete(product);
    }

    private Product getProduct(long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }
}
