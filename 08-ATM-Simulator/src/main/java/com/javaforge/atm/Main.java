package com.javaforge.atm;

import com.javaforge.atm.exception.InsufficientFundsException;
import com.javaforge.atm.exception.InvalidPinException;
import com.javaforge.atm.model.Account;
import com.javaforge.atm.service.ATMService;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Initialize an account with a dummy PIN and balance
        Account myAccount = new Account("123456789", "1234", 1000.0);
        ATMService atmService = new ATMService(myAccount);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Java Forge ATM");
        boolean authenticated = false;

        // Authentication loop
        while (!authenticated) {
            System.out.print("Please enter your PIN (Default is 1234): ");
            String enteredPin = scanner.nextLine();
            try {
                atmService.authenticate(enteredPin);
                authenticated = true;
                System.out.println("Authentication successful!\n");
            } catch (InvalidPinException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again.\n");
            }
        }

        boolean running = true;
        while (running) {
            System.out.println("=== ATM Menu ===");
            System.out.println("1. Check Balance");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int choice = 0;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } catch (InputMismatchException e) {
                System.out.println("Error: Invalid input. Please enter a number.");
                scanner.nextLine(); // consume invalid input
                System.out.println();
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.println("Current Balance: $" + atmService.checkBalance());
                    break;
                case 2:
                    System.out.print("Enter amount to withdraw: $");
                    try {
                        double amount = scanner.nextDouble();
                        scanner.nextLine(); // consume newline
                        atmService.withdraw(amount);
                        System.out.println("Please take your cash. Remaining balance: $" + atmService.checkBalance());
                    } catch (InputMismatchException e) {
                        System.out.println("Error: Invalid input. Please enter a valid amount.");
                        scanner.nextLine(); // consume invalid input
                    } catch (IllegalArgumentException | InsufficientFundsException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.print("Enter amount to deposit: $");
                    try {
                        double amount = scanner.nextDouble();
                        scanner.nextLine(); // consume newline
                        atmService.deposit(amount);
                        System.out.println("Deposit successful. New balance: $" + atmService.checkBalance());
                    } catch (InputMismatchException e) {
                        System.out.println("Error: Invalid input. Please enter a valid amount.");
                        scanner.nextLine(); // consume invalid input
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 4:
                    System.out.println("Thank you for using Java Forge ATM. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
            System.out.println();
        }

        scanner.close();
    }
}
