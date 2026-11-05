# 02 - Library Management

Build a **Library Management System** using Object-Oriented Programming (OOP) principles to manage books, borrowing, and returns from the console.

# Problem Statement

Managing books, member checkouts, and inventory manually leads to book misplacement and inefficient record-keeping. The **Library Management System** is a Java console application that provides an organized way to digitally catalog books, search the inventory, issue books to borrowers, process returns, and manage records.

# Objectives

- Catalog and manage library inventory.
- Demonstrate OOP concepts: Abstraction, Encapsulation, Inheritance, and Polymorphism.
- Add new books to the library.
- View all library books and their status.
- Search for books by ID or title keyword.
- Issue books to borrowers and track availability.
- Process book returns.
- View all currently issued books.
- Delete/remove books from inventory.

# Features

- Add Book
- View All Books
- Search Book (by ID or Title Keyword)
- Issue Book to Borrower
- Return Book
- View Currently Issued Books
- Delete Book
- Exit

# Book Details

- Book ID
- Title
- Author
- Genre / Category
- ISBN
- Availability Status
- Issued To (Borrower Name)

# Tech Stack

- Java (Core Java 21)
- OOP (Abstraction, Encapsulation, Inheritance, Polymorphism)
- Collections (ArrayList)
- Scanner (User Input)
- Control Flow & Loops

# OOP Architecture

```
LibraryItem (abstract)
   │
   └── Book (inherits from LibraryItem)
```

### Class Structure:

- **`LibraryItem` (Abstract Class):**
  - Encapsulates common attributes (`id`, `title`, `author`, `isAvailable`, `issuedTo`).
  - Abstract methods `getItemType()` and `displayDetails()`.
  - Common methods: `issueItem(borrowerName)`, `returnItem()`.
- **`Book` (Class):**
  - Inherits from `LibraryItem`.
  - Additional attributes: `genre`, `isbn`.
  - Polymorphically overrides `getItemType()`, `displayDetails()`, and `toString()`.
- **`LibraryManagement` (Service):**
  - Manages list of books.
  - Implements operations: `addBook()`, `viewAllBooks()`, `searchBook()`, `issueBook()`, `returnBook()`, `viewIssuedBooks()`, `deleteBook()`.
- **`Main` (Runner):**
  - Console menu driver.

# Sample Menu

```
===== Library Management System =====
1. Add Book
2. View All Books
3. Search Book
4. Issue Book
5. Return Book
6. View Issued Books
7. Delete Book
8. Exit
Enter Choice:
```

# Learning Outcomes

By completing this project, you will learn:
- Applying OOP principles (Abstraction, Encapsulation, Inheritance, Polymorphism)
- Designing class hierarchies with abstract classes and method overriding
- State management and business logic validation (issue/return constraints)
- Building robust interactive console applications

## Build and run

Requirements: JDK 21 and Maven. From this directory:

```bash
mvn clean package
java -cp target/classes com.javaforge.library.Main
```

Follow the menu to add or search books, issue a book to a borrower, return it, list issued books, or remove an item. The in-memory catalog resets when the program exits.

## Code walkthrough

- `LibraryItem` is the abstract parent for catalog items and owns shared availability/borrower state.
- `Book` extends the base type with book-specific fields.
- `LibraryManagement` contains catalog and issue/return operations.
- `Main` presents the console menu.

Try issuing a book and then searching for it again. Observe how the availability state changes, and how the service prevents a second issue while the book is already checked out. Return the book and verify that it becomes available again.

## Tests

Run `mvn test` to compile and execute configured tests. This project currently has no automated test classes; manually test missing IDs, repeated issue/return operations, and deletion behavior.
