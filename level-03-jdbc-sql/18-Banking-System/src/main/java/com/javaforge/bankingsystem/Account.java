package com.javaforge.bankingsystem;

import java.util.Objects;

public class Account {
    private int id;
    private String customerName;
    private String accountNumber;
    private double balance;

    public Account() {
    }

    public Account(String customerName, String accountNumber, double balance) {
        this.customerName = customerName;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public Account(int id, String customerName, String accountNumber, double balance) {
        this.id = id;
        this.customerName = customerName;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id && Double.compare(account.balance, balance) == 0 &&
                Objects.equals(customerName, account.customerName) &&
                Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerName, accountNumber, balance);
    }

    @Override
    public String toString() {
        return "Account{id=" + id + ", customerName='" + customerName + "', accountNumber='" + accountNumber + "', balance=" + balance + "}";
    }
}
