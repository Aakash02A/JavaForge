# 02 - Library Management

An Object-Oriented Programming (OOP) console application built with Core Java to manage books, borrowing, and inventory.

## 🚀 Features

- **Add Book:** Add books with ID uniqueness check.
- **View All Books:** View complete inventory with availability and borrower info.
- **Search Book:** Find books by ID or by case-insensitive title keyword.
- **Issue Book:** Checkout books to a borrower with validation.
- **Return Book:** Return checked-out books and update availability.
- **View Issued Books:** List all currently borrowed books and their borrowers.
- **Delete Book:** Remove books from inventory.

## 🛠️ Tech Stack & Concepts

- **Language:** Java 21
- **OOP Concepts:**
  - **Abstraction:** Abstract base class `LibraryItem` with abstract contracts.
  - **Inheritance:** `Book` inherits from `LibraryItem`.
  - **Polymorphism:** Overriding `getItemType()`, `displayDetails()`, `toString()`.
  - **Encapsulation:** State management (`isAvailable`, `issuedTo`, getters/setters).
- **Collections:** `ArrayList`
- **Architecture:** Layered packaging (`model`, `service`, `Main`)

## 📂 Project Structure

```
02-Library-Management/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── javaforge/
                    └── library/
                        ├── model/
                        │   ├── LibraryItem.java
                        │   └── Book.java
                        ├── service/
                        │   └── LibraryManagement.java
                        └── Main.java
```

## ▶️ How to Run

Compile and execute:

```bash
cd 02-Library-Management
javac -d target/classes src/main/java/com/javaforge/library/model/*.java src/main/java/com/javaforge/library/service/*.java src/main/java/com/javaforge/library/*.java
java -cp target/classes com.javaforge.library.Main
```
