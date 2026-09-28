package com.javaforge.bank.service;

import com.javaforge.bank.model.BankAccount;
import com.javaforge.bank.model.CurrentAccount;
import com.javaforge.bank.model.SavingsAccount;
import com.javaforge.bank.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BankService {
    private List<BankAccount> accounts = new ArrayList<>();
    private Scanner sc;

    public BankService() {
        this(new Scanner(System.in));
    }

    public BankService(Scanner sc) {
        this.sc = sc;
        loadTestData();
    }

    private void loadTestData() {
        accounts.add(new SavingsAccount("SA1001", "Alice Johnson", 5000.00, 4.5, 1000.00));
        accounts.add(new SavingsAccount("SA1002", "Bob Smith", 12000.00, 3.8, 500.00));
        accounts.add(new CurrentAccount("CA2001", "Charlie Davis", 25000.00, 10000.00));
        accounts.add(new CurrentAccount("CA2002", "Diana Wilson", 8000.00, 5000.00));
        accounts.add(new SavingsAccount("SA1003", "Ethan Brown", 3200.00, 5.0, 1000.00));
    }

    public BankAccount findAccountByNumber(String accountNumber) {
        for (BankAccount account : accounts) {
            if (account.getAccountNumber().equalsIgnoreCase(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    public void createAccount() {
        System.out.println("Select Account Type:");
        System.out.println("1. Savings Account");
        System.out.println("2. Current Account");
        System.out.print("Enter choice: ");
        int type = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Account Number: ");
        String accNo = sc.nextLine();

        if (findAccountByNumber(accNo) != null) {
            System.out.println("Error: Account with number " + accNo + " already exists!");
            return;
        }

        System.out.print("Enter Account Holder Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Initial Deposit: ");
        double deposit = sc.nextDouble();
        sc.nextLine();

        if (type == 1) {
            System.out.print("Enter Interest Rate (%): ");
            double rate = sc.nextDouble();
            sc.nextLine();
            System.out.print("Enter Minimum Balance: ");
            double minBal = sc.nextDouble();
            sc.nextLine();

            if (deposit < minBal) {
                System.out.println("Error: Initial deposit must meet the minimum balance of $" + String.format("%.2f", minBal) + ".");
                return;
            }

            accounts.add(new SavingsAccount(accNo, name, deposit, rate, minBal));
            System.out.println("Savings Account created successfully...");
        } else if (type == 2) {
            System.out.print("Enter Overdraft Limit: ");
            double overdraft = sc.nextDouble();
            sc.nextLine();

            accounts.add(new CurrentAccount(accNo, name, deposit, overdraft));
            System.out.println("Current Account created successfully...");
        } else {
            System.out.println("Invalid account type!");
        }
    }

    public void deposit() {
        System.out.print("Enter Account Number: ");
        String accNo = sc.nextLine();

        BankAccount account = findAccountByNumber(accNo);
        if (account == null) {
            System.out.println("Account " + accNo + " not found.");
            return;
        }

        System.out.print("Enter Deposit Amount: $");
        double amount = sc.nextDouble();
        sc.nextLine();

        account.deposit(amount);
    }

    public void withdraw() {
        System.out.print("Enter Account Number: ");
        String accNo = sc.nextLine();

        BankAccount account = findAccountByNumber(accNo);
        if (account == null) {
            System.out.println("Account " + accNo + " not found.");
            return;
        }

        System.out.print("Enter Withdrawal Amount: $");
        double amount = sc.nextDouble();
        sc.nextLine();

        account.withdraw(amount);
    }

    public void checkBalance() {
        System.out.print("Enter Account Number: ");
        String accNo = sc.nextLine();

        BankAccount account = findAccountByNumber(accNo);
        if (account == null) {
            System.out.println("Account " + accNo + " not found.");
            return;
        }

        account.displayDetails();
    }

    public void viewAllAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        System.out.println("Total accounts: " + accounts.size());
        for (BankAccount account : accounts) {
            System.out.println(account.toString());
        }
        System.out.println("Accounts listed successfully...");
    }

    public void viewTransactionHistory() {
        System.out.print("Enter Account Number: ");
        String accNo = sc.nextLine();

        BankAccount account = findAccountByNumber(accNo);
        if (account == null) {
            System.out.println("Account " + accNo + " not found.");
            return;
        }

        List<Transaction> transactions = account.getTransactions();
        if (transactions.isEmpty()) {
            System.out.println("No transaction history for account " + accNo + ".");
            return;
        }

        System.out.println("Transaction History for " + account.getAccountHolderName() + " (" + accNo + "):");
        System.out.println("================================================================");
        for (Transaction tx : transactions) {
            System.out.println(tx);
        }
        System.out.println("================================================================");
        System.out.println("Current Balance: $" + String.format("%.2f", account.getBalance()));
    }

    public void transfer() {
        System.out.print("Enter Source Account Number: ");
        String srcAccNo = sc.nextLine();
        BankAccount srcAccount = findAccountByNumber(srcAccNo);
        if (srcAccount == null) {
            System.out.println("Source account " + srcAccNo + " not found.");
            return;
        }

        System.out.print("Enter Destination Account Number: ");
        String destAccNo = sc.nextLine();
        BankAccount destAccount = findAccountByNumber(destAccNo);
        if (destAccount == null) {
            System.out.println("Destination account " + destAccNo + " not found.");
            return;
        }

        if (srcAccNo.equalsIgnoreCase(destAccNo)) {
            System.out.println("Error: Source and destination accounts cannot be the same.");
            return;
        }

        System.out.print("Enter Transfer Amount: $");
        double amount = sc.nextDouble();
        sc.nextLine();

        if (amount <= 0) {
            System.out.println("Transfer amount must be greater than zero.");
            return;
        }

        if (srcAccount.withdraw(amount)) {
            destAccount.deposit(amount);
            srcAccount.addTransaction("TRANSFER OUT", amount);
            destAccount.addTransaction("TRANSFER IN", amount);
            System.out.println("Transfer of $" + String.format("%.2f", amount) +
                    " from " + srcAccNo + " to " + destAccNo + " completed.");
        } else {
            System.out.println("Transfer failed: Insufficient funds in source account.");
        }
    }

    public void applyInterest() {
        int count = 0;
        for (BankAccount account : accounts) {
            if (account instanceof SavingsAccount) {
                ((SavingsAccount) account).applyInterest();
                count++;
            }
        }

        if (count == 0) {
            System.out.println("No savings accounts found to apply interest.");
        } else {
            System.out.println("Interest applied to " + count + " savings account(s).");
        }
    }
}
