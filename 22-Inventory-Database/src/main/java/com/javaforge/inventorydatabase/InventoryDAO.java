package com.javaforge.inventorydatabase;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public InventoryDAO(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public void createTables() {
        String[] statements = {
                "CREATE TABLE IF NOT EXISTS categories (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(100) NOT NULL UNIQUE)",
                "CREATE TABLE IF NOT EXISTS suppliers (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(150) NOT NULL UNIQUE, " +
                        "contact_email VARCHAR(254))",
                "CREATE TABLE IF NOT EXISTS products (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "sku VARCHAR(50) NOT NULL UNIQUE, " +
                        "name VARCHAR(150) NOT NULL, " +
                        "category_id INT NOT NULL REFERENCES categories(id), " +
                        "supplier_id INT NOT NULL REFERENCES suppliers(id), " +
                        "unit_price DECIMAL(12, 2) NOT NULL CHECK (unit_price >= 0))",
                "CREATE TABLE IF NOT EXISTS stock_movements (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "product_id INT NOT NULL REFERENCES products(id), " +
                        "movement_type VARCHAR(3) NOT NULL CHECK (movement_type IN ('IN', 'OUT')), " +
                        "quantity INT NOT NULL CHECK (quantity > 0), " +
                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)"
        };

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            for (String sql : statements) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create inventory tables", e);
        }
    }

    public int addCategory(String name) {
        requireText(name, "Category name");
        return insertNamedRecord("INSERT INTO categories (name) VALUES (?)", name);
    }

    public int addSupplier(String name, String contactEmail) {
        requireText(name, "Supplier name");
        String sql = "INSERT INTO suppliers (name, contact_email) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name.trim());
            pstmt.setString(2, contactEmail == null || contactEmail.isBlank() ? null : contactEmail.trim());
            pstmt.executeUpdate();
            return generatedId(pstmt);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add supplier", e);
        }
    }

    public int addProduct(String sku, String name, int categoryId, int supplierId, BigDecimal unitPrice) {
        requireText(sku, "SKU");
        requireText(name, "Product name");
        if (unitPrice == null || unitPrice.signum() < 0 || unitPrice.scale() > 2) {
            throw new IllegalArgumentException("Unit price must be non-negative with at most two decimal places");
        }

        String sql = "INSERT INTO products (sku, name, category_id, supplier_id, unit_price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, sku.trim());
            pstmt.setString(2, name.trim());
            pstmt.setInt(3, categoryId);
            pstmt.setInt(4, supplierId);
            pstmt.setBigDecimal(5, unitPrice);
            pstmt.executeUpdate();
            return generatedId(pstmt);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add product", e);
        }
    }

    public void recordStockMovement(int productId, StockMovementType type, int quantity) {
        if (type == null) {
            throw new IllegalArgumentException("Movement type must not be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Movement quantity must be positive");
        }

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                lockProduct(conn, productId);
                int currentQuantity = getQuantityOnHand(conn, productId);
                if (type == StockMovementType.OUT && quantity > currentQuantity) {
                    throw new IllegalStateException("Insufficient stock for product id " + productId);
                }

                String sql = "INSERT INTO stock_movements (product_id, movement_type, quantity) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, productId);
                    pstmt.setString(2, type.name());
                    pstmt.setInt(3, quantity);
                    pstmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                rollback(conn, e);
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to record stock movement", e);
        }
    }

    public List<InventoryItem> findAllInventory() {
        String sql = inventorySelect() + inventoryGroupBy() + " ORDER BY p.name, p.sku";
        List<InventoryItem> inventory = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                inventory.add(mapInventoryItem(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list inventory", e);
        }
        return inventory;
    }

    public InventoryItem findInventoryBySku(String sku) {
        requireText(sku, "SKU");
        String sql = inventorySelect() + " WHERE p.sku = ?" + inventoryGroupBy();
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sku.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? mapInventoryItem(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find inventory item by SKU", e);
        }
    }

    private String inventorySelect() {
        return "SELECT p.id AS product_id, p.sku, p.name AS product_name, " +
                "c.name AS category_name, s.name AS supplier_name, p.unit_price, " +
                "COALESCE(SUM(CASE WHEN sm.movement_type = 'IN' THEN sm.quantity " +
                "WHEN sm.movement_type = 'OUT' THEN -sm.quantity ELSE 0 END), 0) AS quantity_on_hand " +
                "FROM products p " +
                "JOIN categories c ON c.id = p.category_id " +
                "JOIN suppliers s ON s.id = p.supplier_id " +
                "LEFT JOIN stock_movements sm ON sm.product_id = p.id ";
    }

    private String inventoryGroupBy() {
        return " GROUP BY p.id, p.sku, p.name, c.name, s.name, p.unit_price";
    }

    private int insertNamedRecord(String sql, String value) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, value.trim());
            pstmt.executeUpdate();
            return generatedId(pstmt);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add inventory record", e);
        }
    }

    private int generatedId(PreparedStatement pstmt) throws SQLException {
        try (ResultSet keys = pstmt.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getInt(1);
            }
        }
        throw new SQLException("Database did not return a generated id");
    }

    private void lockProduct(Connection conn, int productId) throws SQLException {
        String sql = "SELECT id FROM products WHERE id = ? FOR UPDATE";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Product does not exist: " + productId);
                }
            }
        }
    }

    private int getQuantityOnHand(Connection conn, int productId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(CASE WHEN movement_type = 'IN' THEN quantity " +
                "WHEN movement_type = 'OUT' THEN -quantity ELSE 0 END), 0) AS quantity_on_hand " +
                "FROM stock_movements WHERE product_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                return rs.getInt("quantity_on_hand");
            }
        }
    }

    private InventoryItem mapInventoryItem(ResultSet rs) throws SQLException {
        return new InventoryItem(
                rs.getInt("product_id"),
                rs.getString("sku"),
                rs.getString("product_name"),
                rs.getString("category_name"),
                rs.getString("supplier_name"),
                rs.getBigDecimal("unit_price"),
                rs.getInt("quantity_on_hand")
        );
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }

    private static void rollback(Connection conn, Exception originalException) {
        try {
            conn.rollback();
        } catch (SQLException rollbackException) {
            originalException.addSuppressed(rollbackException);
        }
    }
}
