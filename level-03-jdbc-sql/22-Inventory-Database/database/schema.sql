CREATE DATABASE IF NOT EXISTS inventory_database;
USE inventory_database;

-- Reference data is kept separately to avoid repeating names in each product row.
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS suppliers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    contact_email VARCHAR(254)
);

CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    category_id INT NOT NULL,
    supplier_id INT NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL CHECK (unit_price >= 0),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_products_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

-- On-hand quantity is derived from movements instead of duplicated in products.
CREATE TABLE IF NOT EXISTS stock_movements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    movement_type VARCHAR(3) NOT NULL CHECK (movement_type IN ('IN', 'OUT')),
    quantity INT NOT NULL CHECK (quantity > 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movements_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Example setup and CRUD/report queries:
-- INSERT INTO categories (name) VALUES ('Electronics');
-- INSERT INTO suppliers (name, contact_email) VALUES ('Northwind Supply', 'sales@example.com');
-- INSERT INTO products (sku, name, category_id, supplier_id, unit_price)
-- VALUES ('KB-100', 'Mechanical Keyboard', 1, 1, 79.99);
-- INSERT INTO stock_movements (product_id, movement_type, quantity) VALUES (1, 'IN', 12);
-- INSERT INTO stock_movements (product_id, movement_type, quantity) VALUES (1, 'OUT', 3);
--
-- Normalized inventory report, joining product with category and supplier
-- while calculating the current quantity from its movement history:
-- SELECT p.id, p.sku, p.name AS product_name, c.name AS category,
--        s.name AS supplier, p.unit_price,
--        COALESCE(SUM(CASE WHEN sm.movement_type = 'IN' THEN sm.quantity
--                          WHEN sm.movement_type = 'OUT' THEN -sm.quantity
--                          ELSE 0 END), 0) AS quantity_on_hand
-- FROM products p
-- JOIN categories c ON c.id = p.category_id
-- JOIN suppliers s ON s.id = p.supplier_id
-- LEFT JOIN stock_movements sm ON sm.product_id = p.id
-- GROUP BY p.id, p.sku, p.name, c.name, s.name, p.unit_price
-- ORDER BY p.name, p.sku;
--
-- UPDATE products SET unit_price = 84.99 WHERE sku = 'KB-100';
-- DELETE FROM products WHERE sku = 'KB-100';
