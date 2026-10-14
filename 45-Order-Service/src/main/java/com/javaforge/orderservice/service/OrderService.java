package com.javaforge.orderservice.service;

import com.javaforge.orderservice.client.ProductClient;
import com.javaforge.orderservice.dto.CreateOrderRequest;
import com.javaforge.orderservice.dto.OrderItemRequest;
import com.javaforge.orderservice.dto.OrderResponse;
import com.javaforge.orderservice.dto.ProductResponse;
import com.javaforge.orderservice.exception.InsufficientStockException;
import com.javaforge.orderservice.exception.DuplicateOrderProductException;
import com.javaforge.orderservice.exception.OrderNotFoundException;
import com.javaforge.orderservice.model.CustomerOrder;
import com.javaforge.orderservice.model.OrderItem;
import com.javaforge.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public OrderService(OrderRepository orderRepository, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
    }

    public OrderResponse create(CreateOrderRequest request) {
        ensureDistinctProducts(request.items());
        CustomerOrder order = new CustomerOrder(request.customerEmail());

        for (OrderItemRequest itemRequest : request.items()) {
            ProductResponse product = productClient.getProduct(itemRequest.productId());
            if (itemRequest.quantity() > product.stockQuantity()) {
                throw new InsufficientStockException(
                        product.id(),
                        itemRequest.quantity(),
                        product.stockQuantity()
                );
            }
            order.addItem(new OrderItem(
                    product.id(),
                    product.sku(),
                    product.name(),
                    product.price(),
                    itemRequest.quantity()
            ));
        }

        return OrderResponse.from(orderRepository.saveAndFlush(order));
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(long id) {
        return orderRepository.findById(id)
                .map(OrderResponse::from)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::from)
                .toList();
    }

    private void ensureDistinctProducts(List<OrderItemRequest> items) {
        Set<Long> productIds = new HashSet<>();
        for (OrderItemRequest item : items) {
            if (!productIds.add(item.productId())) {
                throw new DuplicateOrderProductException(item.productId());
            }
        }
    }
}
