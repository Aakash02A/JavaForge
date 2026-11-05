package com.javaforge.inventory.model;

public class Electronics extends Product {
    private int warrantyMonths;

    public Electronics(String id, String name, double price, int warrantyMonths) {
        super(id, name, price);
        this.warrantyMonths = warrantyMonths;
    }

    public int getWarrantyMonths() { return warrantyMonths; }

    @Override
    public String toString() {
        return "Electronics{" + super.toString() + ", warrantyMonths=" + warrantyMonths + "}";
    }
}
