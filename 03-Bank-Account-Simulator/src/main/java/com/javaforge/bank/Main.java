package com.javaforge.bank;

import com.javaforge.bank.service.BankService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean proceed = true;

        BankService bank = new BankService(sc);

        while (proceed) {
            System.out.println("\n===== Bank Account Simulator =====");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Check Balance");
            System.out.println("6. View All Accounts");
            System.out.println("7. Transaction History");
            System.out.println("8. Apply Interest (Savings)");
            System.out.println("9. Exit");

            System.out.print("Enter Choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("===== Create Account =====");
                    bank.createAccount();
                    break;
                case 2:
                    System.out.println("===== Deposit =====");
                    bank.deposit();
                    break;
                case 3:
                    System.out.println("===== Withdraw =====");
                    bank.withdraw();
                    break;
                case 4:
                    System.out.println("===== Transfer =====");
                    bank.transfer();
                    break;
                case 5:
                    System.out.println("===== Check Balance =====");
                    bank.checkBalance();
                    break;
                case 6:
                    System.out.println("===== All Accounts =====");
                    bank.viewAllAccounts();
                    break;
                case 7:
                    System.out.println("===== Transaction History =====");
                    bank.viewTransactionHistory();
                    break;
                case 8:
                    System.out.println("===== Apply Interest =====");
                    bank.applyInterest();
                    break;
                case 9:
                    System.out.println("Exiting Bank Account Simulator. Goodbye!");
                    proceed = false;
                    break;
                default:
                    System.out.println("Invalid Choice! Please enter a number between 1 and 9.");
            }
        }
    }
}
