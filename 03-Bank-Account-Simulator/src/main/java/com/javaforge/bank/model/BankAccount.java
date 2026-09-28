package com.javaforge.bank.model;

import java.util.ArrayList;
import java.util.List;

public abstract class BankAccount {
    private String accountNumber;
    private String accountHolderName;
    protected double balance;
    private List<Transaction> transactions;

    public BankAccount(String accountNumber, String accountHolderName, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialDeposit;
        this.transactions = new ArrayList<>();
        if (initialDeposit > 0) {
            transactions.add(new Transaction("INITIAL DEPOSIT", initialDeposit, balance));
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(String type, double amount) {
        transactions.add(new Transaction(type, amount, balance));
    }

    public boolean deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Deposit amount must be greater than zero.");
            return false;
        }
        balance += amount;
        addTransaction("DEPOSIT", amount);
        System.out.println("Successfully deposited $" + String.format("%.2f", amount) + " into account " + accountNumber + ".");
        System.out.println("New Balance: $" + String.format("%.2f", balance));
        return true;
    }

    public abstract boolean withdraw(double amount);

    public abstract String getAccountType();

    public void displayDetails() {
        System.out.println("----------------------------------------");
        System.out.println("Account Type   : " + getAccountType());
        System.out.println("Account Number : " + accountNumber);
        System.out.println("Holder Name    : " + accountHolderName);
        System.out.println("Current Balance: $" + String.format("%.2f", balance));
    }

    @Override
    public String toString() {
        return "[" + getAccountType() + "] Acc No: " + accountNumber +
                ", Holder: " + accountHolderName +
                ", Balance: $" + String.format("%.2f", balance);
    }
}
