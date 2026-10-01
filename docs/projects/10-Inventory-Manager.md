# Project Idea

Build an **Inventory Manager** that can manage products of different types using Java Generics.

# Problem Statement

A store sells different categories of products — electronics, clothing, and groceries. Building a separate inventory system for each type leads to code duplication.

The **Inventory Manager** uses Java Generics to build a single, reusable inventory that can manage any type of product while maintaining full type safety at compile time.

# Objectives

- Add items to a typed inventory.
- Remove items from inventory.
- Find items by ID.
- List all items.
- Calculate total inventory value.

# Features

- Generic Inventory (works for any Product subtype)
- Add / Remove Items
- Search by ID
- Total Value Calculator
- Type-Safe Collections

# Product Details

Product (Abstract Base)
- ID
- Name
- Price

Electronics (extends Product)
- Warranty Months

Clothing (extends Product)
- Size

# Tech Stack

- Java (Intermediate)
- Generics (bounded type parameters)
- Abstract Classes & Inheritance
- Java Streams
- Optional

# Suggested Class Structure

Product (abstract)
------------------
id
name
price

Electronics extends Product
----------------------------
warrantyMonths

Clothing extends Product
-------------------------
size

Inventory<T extends Product>
-----------------------------
addItem()
removeItem()
findById()
getAllItems()
calculateTotalValue()

Main
----
main()

# Sample Output

=== Java Forge Inventory Manager (Generics) ===

Electronics Inventory:
Electronics{id='E01', name='Smartphone', price=799.99, warrantyMonths=24}
Electronics{id='E02', name='Laptop', price=1299.99, warrantyMonths=36}
Total Value: $2099.98

Clothing Inventory:
Clothing{id='C01', name='T-Shirt', price=19.99, size='M'}
Total Value: $69.98

Searching for Electronics with ID 'E01':
Found: Electronics{id='E01', name='Smartphone', price=799.99}

# Learning Outcomes

By completing this project, you will learn:

- Writing generic classes with bounded type parameters (`<T extends Product>`)
- Preventing code duplication with reusable generic structures
- Using Optional for safe null handling
- How Java enforces type safety at compile time with generics
- Combining Generics with Streams

# Project Details

Difficulty: ⭐⭐☆☆☆ (Intermediate)

Estimated Time: 4–6 hours

Skills Gained:
- Java Generics
- Bounded Type Parameters
- Abstract Classes
- Type Safety
