package com.javaforge.bank.service;

import com.javaforge.bank.model.BankAccount;
import com.javaforge.bank.model.CurrentAccount;
import com.javaforge.bank.model.SavingsAccount;
import com.javaforge.bank.model.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class BankService {
    private List<BankAccount> accounts = new ArrayList<>();
    private Scanner sc;

    public BankService() {
        this(new Scanner(System.in));
    }

    public BankService(Scanner sc) {
        this(sc, true);
    }

    public BankService(Scanner sc, boolean loadDemoData) {
        this.sc = sc;
        if (loadDemoData) {
            loadTestData();
        }
    }

    private void loadTestData() {
        accounts.add(new SavingsAccount("SA1001", "Alice Johnson", 5000.00, 4.5, 1000.00));
        accounts.add(new SavingsAccount("SA1002", "Bob Smith", 12000.00, 3.8, 500.00));
        accounts.add(new CurrentAccount("CA2001", "Charlie Davis", 25000.00, 10000.00));
        accounts.add(new CurrentAccount("CA2002", "Diana Wilson", 8000.00, 5000.00));
        accounts.add(new SavingsAccount("SA1003", "Ethan Brown", 3200.00, 5.0, 1000.00));
    }

    public List<BankAccount> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public boolean addAccount(BankAccount account) {
        if (account == null || findAccountByNumber(account.getAccountNumber()) != null) {
            return false;
        }
        accounts.add(account);
        return true;
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
        int type = readInt("Enter choice: ");

        String accNo = readRequiredText("Enter Account Number: ");

        if (findAccountByNumber(accNo) != null) {
            System.out.println("Error: Account with number " + accNo + " already exists!");
            return;
        }

        String name = readRequiredText("Enter Account Holder Name: ");
        double deposit = readDouble("Enter Initial Deposit: ");

        if (type == 1) {
            double rate = readDouble("Enter Interest Rate (%): ");
            double minBal = readDouble("Enter Minimum Balance: ");

            if (deposit < minBal || deposit < 0 || rate < 0 || minBal < 0) {
                System.out.println("Error: Initial deposit must meet the minimum balance of $" + String.format("%.2f", minBal) + ".");
                return;
            }

            addAccount(new SavingsAccount(accNo, name, deposit, rate, minBal));
            System.out.println("Savings Account created successfully...");
        } else if (type == 2) {
            double overdraft = readDouble("Enter Overdraft Limit: ");

            if (deposit < 0 || overdraft < 0) {
                System.out.println("Error: Deposit and overdraft limit cannot be negative.");
                return;
            }
            addAccount(new CurrentAccount(accNo, name, deposit, overdraft));
            System.out.println("Current Account created successfully...");
        } else {
            System.out.println("Invalid account type!");
        }
    }

    public void deposit() {
        String accNo = readRequiredText("Enter Account Number: ");

        BankAccount account = findAccountByNumber(accNo);
        if (account == null) {
            System.out.println("Account " + accNo + " not found.");
            return;
        }

        double amount = readDouble("Enter Deposit Amount: $");

        account.deposit(amount);
    }

    public void withdraw() {
        String accNo = readRequiredText("Enter Account Number: ");

        BankAccount account = findAccountByNumber(accNo);
        if (account == null) {
            System.out.println("Account " + accNo + " not found.");
            return;
        }

        double amount = readDouble("Enter Withdrawal Amount: $");

        account.withdraw(amount);
    }

    public void checkBalance() {
        String accNo = readRequiredText("Enter Account Number: ");

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
        String accNo = readRequiredText("Enter Account Number: ");

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
        String srcAccNo = readRequiredText("Enter Source Account Number: ");
        BankAccount srcAccount = findAccountByNumber(srcAccNo);
        if (srcAccount == null) {
            System.out.println("Source account " + srcAccNo + " not found.");
            return;
        }

        String destAccNo = readRequiredText("Enter Destination Account Number: ");
        BankAccount destAccount = findAccountByNumber(destAccNo);
        if (destAccount == null) {
            System.out.println("Destination account " + destAccNo + " not found.");
            return;
        }

        if (srcAccNo.equalsIgnoreCase(destAccNo)) {
            System.out.println("Error: Source and destination accounts cannot be the same.");
            return;
        }

        double amount = readDouble("Enter Transfer Amount: $");

        if (!transfer(srcAccNo, destAccNo, amount)) {
            return;
        }

        System.out.println("Transfer of $" + String.format("%.2f", amount) +
                " from " + srcAccNo + " to " + destAccNo + " completed.");
    }

    public boolean transfer(String sourceAccountNumber, String destinationAccountNumber, double amount) {
        BankAccount srcAccount = findAccountByNumber(sourceAccountNumber);
        BankAccount destAccount = findAccountByNumber(destinationAccountNumber);

        if (srcAccount == null || destAccount == null
                || sourceAccountNumber == null || destinationAccountNumber == null
                || sourceAccountNumber.equalsIgnoreCase(destinationAccountNumber)) {
            System.out.println("Transfer failed: valid, different source and destination accounts are required.");
            return false;
        }
        if (amount <= 0) {
            System.out.println("Transfer amount must be greater than zero.");
            return false;
        }

        if (srcAccount.withdraw(amount)) {
            destAccount.deposit(amount);
            return true;
        } else {
            System.out.println("Transfer failed: Insufficient funds in source account.");
            return false;
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

    private String readRequiredText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Value is required.");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            try {
                double amount = Double.parseDouble(value);
                if (Double.isFinite(amount)) {
                    return amount;
                }
            } catch (NumberFormatException ignored) {
                // Keep the prompt interactive and show the same validation message below.
            }
            System.out.println("Please enter a valid number.");
        }
    }
}
