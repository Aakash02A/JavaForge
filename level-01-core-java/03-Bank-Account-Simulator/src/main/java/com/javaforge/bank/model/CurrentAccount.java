package com.javaforge.bank.model;

public class CurrentAccount extends BankAccount {
    private double overdraftLimit;

    public CurrentAccount(String accountNumber, String accountHolderName, double initialDeposit, double overdraftLimit) {
        super(accountNumber, accountHolderName, initialDeposit);
        if (overdraftLimit < 0) {
            throw new IllegalArgumentException("Overdraft limit cannot be negative.");
        }
        this.overdraftLimit = overdraftLimit;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        if (overdraftLimit < 0) {
            throw new IllegalArgumentException("Overdraft limit cannot be negative.");
        }
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be greater than zero.");
            return false;
        }

        double availableFunds = balance + overdraftLimit;
        if (amount > availableFunds) {
            System.out.println("Withdrawal denied: Exceeds overdraft limit.");
            System.out.println("Current Balance: $" + String.format("%.2f", balance) +
                    ", Overdraft Limit: $" + String.format("%.2f", overdraftLimit) +
                    ", Total Available: $" + String.format("%.2f", availableFunds));
            return false;
        }

        balance -= amount;
        addTransaction("WITHDRAWAL", amount);
        System.out.println("Successfully withdrew $" + String.format("%.2f", amount) + " from account " + getAccountNumber() + ".");
        System.out.println("Remaining Balance: $" + String.format("%.2f", balance));
        return true;
    }

    @Override
    public String getAccountType() {
        return "Current Account";
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Overdraft Limit: $" + String.format("%.2f", overdraftLimit));
        System.out.println("Available Limit: $" + String.format("%.2f", balance + overdraftLimit));
        System.out.println("----------------------------------------");
    }
}
