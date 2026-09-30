package com.javaforge.inventory.model;

public class Clothing extends Product {
    private String size;

    public Clothing(String id, String name, double price, String size) {
        super(id, name, price);
        this.size = size;
    }

    public String getSize() { return size; }

    @Override
    public String toString() {
        return "Clothing{" + super.toString() + ", size='" + size + "'}";
    }
}
