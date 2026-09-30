package com.javaforge.atm.service;

import com.javaforge.atm.exception.InsufficientFundsException;
import com.javaforge.atm.exception.InvalidPinException;
import com.javaforge.atm.model.Account;

public class ATMService {
    private Account account;

    public ATMService(Account account) {
        this.account = account;
    }

    public void authenticate(String pin) throws InvalidPinException {
        if (!account.getPin().equals(pin)) {
            throw new InvalidPinException("Incorrect PIN entered.");
        }
    }

    public double checkBalance() {
        return account.getBalance();
    }

    public void withdraw(double amount) throws InsufficientFundsException, IllegalArgumentException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > account.getBalance()) {
            throw new InsufficientFundsException("Insufficient funds. Current balance: $" + account.getBalance());
        }
        account.setBalance(account.getBalance() - amount);
    }

    public void deposit(double amount) throws IllegalArgumentException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        account.setBalance(account.getBalance() + amount);
    }
}
