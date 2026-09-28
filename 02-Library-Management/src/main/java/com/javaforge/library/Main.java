package com.javaforge.library;

import com.javaforge.library.service.LibraryManagement;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean proceed = true;

        LibraryManagement lm = new LibraryManagement(sc);

        while (proceed) {
            System.out.println("\n===== Library Management System =====");
            System.out.println("1. Add Book");
            System.out.println("2. View All Books");
            System.out.println("3. Search Book");
            System.out.println("4. Issue Book");
            System.out.println("5. Return Book");
            System.out.println("6. View Issued Books");
            System.out.println("7. Delete Book");
            System.out.println("8. Exit");

            System.out.print("Enter Choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("===== Add Book =====");
                    lm.addBook();
                    break;
                case 2:
                    System.out.println("===== View All Books =====");
                    lm.viewAllBooks();
                    break;
                case 3:
                    System.out.println("===== Search Book =====");
                    lm.searchBook();
                    break;
                case 4:
                    System.out.println("===== Issue Book =====");
                    lm.issueBook();
                    break;
                case 5:
                    System.out.println("===== Return Book =====");
                    lm.returnBook();
                    break;
                case 6:
                    lm.viewIssuedBooks();
                    break;
                case 7:
                    System.out.println("===== Delete Book =====");
                    lm.deleteBook();
                    break;
                case 8:
                    System.out.println("Exiting Library Management System. Goodbye!");
                    proceed = false;
                    break;
                default:
                    System.out.println("Invalid Choice! Please enter a number between 1 and 8.");
            }
        }
    }
}
