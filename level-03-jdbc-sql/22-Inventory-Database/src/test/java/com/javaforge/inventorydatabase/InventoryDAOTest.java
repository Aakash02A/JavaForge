package com.javaforge.inventorydatabase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryDAOTest {
    private static final String URL = "jdbc:h2:mem:inventorydb;DB_CLOSE_DELAY=-1";
    private InventoryDAO dao;
    private int categoryId;
    private int supplierId;
    private int productId;

    @BeforeEach
    void setUp() {
        dao = new InventoryDAO(URL, "sa", "");
        dao.createTables();
        categoryId = dao.addCategory("Electronics");
        supplierId = dao.addSupplier("Northwind Supply", "sales@example.com");
        productId = dao.addProduct("KB-100", "Mechanical Keyboard", categoryId, supplierId,
                new BigDecimal("79.99"));
    }

    @AfterEach
    void tearDown() throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, "sa", "");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE stock_movements");
            stmt.execute("DROP TABLE products");
            stmt.execute("DROP TABLE suppliers");
            stmt.execute("DROP TABLE categories");
        }
    }

    @Test
    void storesNormalizedRecordsAndJoinsInventoryDetails() {
        dao.recordStockMovement(productId, StockMovementType.IN, 12);

        List<InventoryItem> inventory = dao.findAllInventory();
        assertEquals(1, inventory.size());
        InventoryItem item = inventory.get(0);
        assertEquals("KB-100", item.getSku());
        assertEquals("Electronics", item.getCategoryName());
        assertEquals("Northwind Supply", item.getSupplierName());
        assertEquals(new BigDecimal("79.99"), item.getUnitPrice());
        assertEquals(12, item.getQuantityOnHand());
    }

    @Test
    void calculatesStockAcrossInboundAndOutboundMovements() {
        dao.recordStockMovement(productId, StockMovementType.IN, 10);
        dao.recordStockMovement(productId, StockMovementType.OUT, 3);

        assertEquals(7, dao.findInventoryBySku("KB-100").getQuantityOnHand());
    }

    @Test
    void rejectsOutboundMovementThatWouldMakeStockNegative() {
        dao.recordStockMovement(productId, StockMovementType.IN, 4);

        assertThrows(IllegalStateException.class,
                () -> dao.recordStockMovement(productId, StockMovementType.OUT, 5));
        assertEquals(4, dao.findInventoryBySku("KB-100").getQuantityOnHand());
    }

    @Test
    void representsProductsWithoutStockMovementsWithZeroQuantity() {
        InventoryItem item = dao.findInventoryBySku("KB-100");

        assertNotNull(item);
        assertEquals(0, item.getQuantityOnHand());
    }

    @Test
    void rejectsInvalidMovementQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.recordStockMovement(productId, StockMovementType.IN, 0));
    }
}
