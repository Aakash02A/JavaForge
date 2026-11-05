package com.javaforge.inventorydatabase;

import java.math.BigDecimal;

public class InventoryItem {
    private final int productId;
    private final String sku;
    private final String productName;
    private final String categoryName;
    private final String supplierName;
    private final BigDecimal unitPrice;
    private final int quantityOnHand;

    public InventoryItem(int productId, String sku, String productName,
                         String categoryName, String supplierName,
                         BigDecimal unitPrice, int quantityOnHand) {
        this.productId = productId;
        this.sku = sku;
        this.productName = productName;
        this.categoryName = categoryName;
        this.supplierName = supplierName;
        this.unitPrice = unitPrice;
        this.quantityOnHand = quantityOnHand;
    }

    public int getProductId() {
        return productId;
    }

    public String getSku() {
        return sku;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantityOnHand() {
        return quantityOnHand;
    }
}
