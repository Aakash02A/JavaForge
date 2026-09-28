package com.javaforge.bank.model;

public class SavingsAccount extends BankAccount {
    private double interestRate;
    private double minimumBalance;

    public SavingsAccount(String accountNumber, String accountHolderName, double initialDeposit, double interestRate, double minimumBalance) {
        super(accountNumber, accountHolderName, initialDeposit);
        this.interestRate = interestRate;
        this.minimumBalance = minimumBalance;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(double minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be greater than zero.");
            return false;
        }

        if (balance - amount < minimumBalance) {
            System.out.println("Withdrawal denied: Minimum balance requirement of $" +
                    String.format("%.2f", minimumBalance) + " must be maintained.");
            System.out.println("Available for withdrawal: $" +
                    String.format("%.2f", Math.max(0, balance - minimumBalance)));
            return false;
        }

        balance -= amount;
        addTransaction("WITHDRAWAL", amount);
        System.out.println("Successfully withdrew $" + String.format("%.2f", amount) + " from account " + getAccountNumber() + ".");
        System.out.println("Remaining Balance: $" + String.format("%.2f", balance));
        return true;
    }

    public void applyInterest() {
        double interest = (balance * interestRate) / 100.0;
        if (interest > 0) {
            balance += interest;
            addTransaction("INTEREST", interest);
            System.out.println("Interest of $" + String.format("%.2f", interest) + " applied at " + interestRate + "% to Acc No: " + getAccountNumber() + ".");
            System.out.println("New Balance: $" + String.format("%.2f", balance));
        }
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Interest Rate  : " + interestRate + "%");
        System.out.println("Min Balance Req: $" + String.format("%.2f", minimumBalance));
        System.out.println("----------------------------------------");
    }
}
