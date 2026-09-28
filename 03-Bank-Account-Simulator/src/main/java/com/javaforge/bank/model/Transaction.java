package com.javaforge.bank.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String type;
    private double amount;
    private double balanceAfter;
    private LocalDateTime timestamp;

    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "[" + timestamp.format(formatter) + "] " +
                String.format("%-10s", type) + " | Amount: $" + String.format("%.2f", amount) +
                " | Balance: $" + String.format("%.2f", balanceAfter);
    }
}
