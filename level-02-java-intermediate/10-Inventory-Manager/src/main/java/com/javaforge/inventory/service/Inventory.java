package com.javaforge.inventory.service;

import com.javaforge.inventory.model.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Bounded generic type: T must be a subclass of Product
public class Inventory<T extends Product> {
    private List<T> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public void addItem(T item) {
        items.add(item);
    }

    public void removeItem(T item) {
        items.remove(item);
    }

    public Optional<T> findById(String id) {
        return items.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst();
    }

    public List<T> getAllItems() {
        return new ArrayList<>(items);
    }

    public double calculateTotalValue() {
        return items.stream()
                .mapToDouble(Product::getPrice)
                .sum();
    }
}
