package com.javaforge.library.service;

import com.javaforge.library.model.Book;

import java.util.ArrayList;
import java.util.Scanner;

public class LibraryManagement {
    private ArrayList<Book> books = new ArrayList<>();
    private Scanner sc;

    public LibraryManagement() {
        this(new Scanner(System.in));
    }

    public LibraryManagement(Scanner sc) {
        this.sc = sc;
        loadTestData();
    }

    private void loadTestData() {
        books.add(new Book(101, "Clean Code", "Robert C. Martin", "Software Engineering", "978-0132350884"));
        books.add(new Book(102, "Effective Java", "Joshua Bloch", "Programming", "978-0134685991"));
        books.add(new Book(103, "Design Patterns", "Erich Gamma et al.", "Software Architecture", "978-0201633610"));
        books.add(new Book(104, "The Pragmatic Programmer", "Andrew Hunt, David Thomas", "Software Engineering", "978-0135957059"));
        books.add(new Book(105, "Introduction to Algorithms", "Thomas H. Cormen", "Computer Science", "978-0262046305"));
    }

    public Book findBookById(int id) {
        for (Book book : books) {
            if (book.getId() == id) {
                return book;
            }
        }
        return null;
    }

    public void addBook() {
        System.out.print("Enter Book ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        if (findBookById(id) != null) {
            System.out.println("Error: Book with ID " + id + " already exists!");
            return;
        }

        System.out.print("Enter Title: ");
        String title = sc.nextLine();
        System.out.print("Enter Author: ");
        String author = sc.nextLine();
        System.out.print("Enter Genre/Category: ");
        String genre = sc.nextLine();
        System.out.print("Enter ISBN: ");
        String isbn = sc.nextLine();

        Book book = new Book(id, title, author, genre, isbn);
        books.add(book);
        System.out.println("Book added successfully...");
    }

    public void viewAllBooks() {
        if (books.isEmpty()) {
            System.out.println("No books available in the library.");
            return;
        }

        System.out.println("Total books in library: " + books.size());
        for (Book book : books) {
            System.out.println(book.toString());
        }
        System.out.println("Books listed successfully...");
    }

    public void searchBook() {
        System.out.println("Search by:");
        System.out.println("1. Book ID");
        System.out.println("2. Title Keyword");
        System.out.print("Enter choice: ");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1) {
            System.out.print("Enter Book ID to search: ");
            int id = sc.nextInt();
            sc.nextLine();

            Book book = findBookById(id);
            if (book != null) {
                System.out.println("Book found:");
                book.displayDetails();
            } else {
                System.out.println("Book with ID " + id + " not found.");
            }
        } else if (choice == 2) {
            System.out.print("Enter Title keyword: ");
            String keyword = sc.nextLine().toLowerCase();

            boolean found = false;
            for (Book book : books) {
                if (book.getTitle().toLowerCase().contains(keyword)) {
                    book.displayDetails();
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No books matching \"" + keyword + "\" were found.");
            }
        } else {
            System.out.println("Invalid search choice.");
        }
    }

    public void issueBook() {
        System.out.print("Enter Book ID to issue: ");
        int id = sc.nextInt();
        sc.nextLine();

        Book book = findBookById(id);
        if (book == null) {
            System.out.println("Book with ID " + id + " not found.");
            return;
        }

        if (!book.isAvailable()) {
            System.out.println("Sorry, \"" + book.getTitle() + "\" is already issued to " + book.getIssuedTo() + ".");
            return;
        }

        System.out.print("Enter Borrower Name: ");
        String borrower = sc.nextLine();

        if (book.issueItem(borrower)) {
            System.out.println("Book \"" + book.getTitle() + "\" successfully issued to " + borrower + ".");
        } else {
            System.out.println("Failed to issue book.");
        }
    }

    public void returnBook() {
        System.out.print("Enter Book ID to return: ");
        int id = sc.nextInt();
        sc.nextLine();

        Book book = findBookById(id);
        if (book == null) {
            System.out.println("Book with ID " + id + " not found.");
            return;
        }

        if (book.isAvailable()) {
            System.out.println("Book \"" + book.getTitle() + "\" is already marked as Available in the library.");
            return;
        }

        String previousBorrower = book.getIssuedTo();
        if (book.returnItem()) {
            System.out.println("Book \"" + book.getTitle() + "\" returned successfully from " + previousBorrower + ".");
        } else {
            System.out.println("Failed to return book.");
        }
    }

    public void viewIssuedBooks() {
        int count = 0;
        System.out.println("===== Currently Issued Books =====");
        for (Book book : books) {
            if (!book.isAvailable()) {
                System.out.println("ID: " + book.getId() +
                        ", Title: \"" + book.getTitle() + "\"" +
                        ", Borrower: " + book.getIssuedTo());
                count++;
            }
        }

        if (count == 0) {
            System.out.println("No books are currently issued.");
        } else {
            System.out.println("Total issued books: " + count);
        }
    }

    public void deleteBook() {
        System.out.print("Enter Book ID to delete: ");
        int id = sc.nextInt();
        sc.nextLine();

        Book book = findBookById(id);
        if (book == null) {
            System.out.println("Book with ID " + id + " not found.");
            return;
        }

        books.remove(book);
        System.out.println("Book \"" + book.getTitle() + "\" (ID: " + id + ") removed from library successfully.");
    }
}
