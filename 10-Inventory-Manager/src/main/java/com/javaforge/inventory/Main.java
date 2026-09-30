package com.javaforge.inventory;

import com.javaforge.inventory.model.Clothing;
import com.javaforge.inventory.model.Electronics;
import com.javaforge.inventory.service.Inventory;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Java Forge Inventory Manager (Generics) ===");

        // 1. Create an Inventory specifically for Electronics
        Inventory<Electronics> techInventory = new Inventory<>();
        techInventory.addItem(new Electronics("E01", "Smartphone", 799.99, 24));
        techInventory.addItem(new Electronics("E02", "Laptop", 1299.99, 36));

        System.out.println("\nElectronics Inventory:");
        techInventory.getAllItems().forEach(System.out::println);
        System.out.printf("Total Value: $%.2f%n", techInventory.calculateTotalValue());

        // 2. Create an Inventory specifically for Clothing
        Inventory<Clothing> clothingInventory = new Inventory<>();
        clothingInventory.addItem(new Clothing("C01", "T-Shirt", 19.99, "M"));
        clothingInventory.addItem(new Clothing("C02", "Jeans", 49.99, "L"));

        System.out.println("\nClothing Inventory:");
        clothingInventory.getAllItems().forEach(System.out::println);
        System.out.printf("Total Value: $%.2f%n", clothingInventory.calculateTotalValue());
        
        // 3. Demonstrate generic lookup and removal
        System.out.println("\nSearching for Electronics with ID 'E01':");
        techInventory.findById("E01").ifPresentOrElse(
                item -> {
                    System.out.println("Found: " + item);
                    System.out.println("Removing 'E01'...");
                    techInventory.removeItem(item);
                },
                () -> System.out.println("Not found.")
        );
        
        System.out.println("Searching for removed item 'E01':");
        techInventory.findById("E01").ifPresentOrElse(
                item -> System.out.println("Found: " + item),
                () -> System.out.println("Not found.")
        );

        // 4. Test Edge Cases (Empty Inventory)
        System.out.println("\n=== Testing Edge Cases (Empty Inventory) ===");
        Inventory<Clothing> emptyInventory = new Inventory<>();
        System.out.println("Empty Inventory Items: " + emptyInventory.getAllItems());
        System.out.println("Empty Inventory Value: $" + emptyInventory.calculateTotalValue());
        
        System.out.println("Searching in Empty Inventory:");
        emptyInventory.findById("UNKNOWN").ifPresentOrElse(
                item -> System.out.println("Found: " + item),
                () -> System.out.println("Not found.")
        );
    }
}
