# Project Idea

Build an **Inventory Database** that tracks products, categories, suppliers, and stock movements using a normalized relational schema.

# Problem Statement

Storing repeated category or supplier information with every product causes duplication and inconsistent updates. This project separates related data into normalized tables and derives on-hand stock from a movement ledger.

# Objectives

- Connect Java to MySQL using JDBC.
- Normalize product, category, supplier, and stock movement data.
- Use primary and foreign keys to maintain referential integrity.
- Query an inventory report with joins and aggregate stock movements.
- Record inbound and outbound stock and prevent negative inventory.

# Features

- Add categories, suppliers, and products.
- Record inbound and outbound stock movements.
- Find inventory by SKU or list the complete inventory.
- Show supplier and category details in a joined inventory report.
- Calculate stock on hand from the stock movement history.
- Reject outbound movements that exceed available stock.

# Tech Stack

- Java (Core Java, JDBC)
- MySQL 8.0+ with MySQL Connector/J
- H2 Database for isolated automated tests
- JUnit 5 for Testing
- Normalized SQL Schema / DAO Pattern

# MySQL Setup

Run `database/schema.sql` in MySQL to create the `inventory_database` schema and its normalized tables. Use the following connection setup:

```java
InventoryDAO dao = new InventoryDAO(
        "jdbc:mysql://localhost:3306/inventory_database",
        "your_mysql_user",
        "your_mysql_password"
);
```

The schema script includes example insert and inventory-report SQL queries. The inventory report joins products, categories, suppliers, and stock movements; quantity on hand is calculated from the movement ledger rather than stored redundantly.

# Project Details

Difficulty: ⭐⭐⭐☆☆ (Intermediate)

Estimated Time: 3–5 hours
