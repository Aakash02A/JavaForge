package com.javaforge.bank;

import com.javaforge.bank.model.CurrentAccount;
import com.javaforge.bank.model.SavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankAccountTest {
    @Test
    void savingsAccountEnforcesMinimumBalance() {
        SavingsAccount account = new SavingsAccount("SA-1", "Alex", 1000, 5, 500);

        assertFalse(account.withdraw(501));
        assertTrue(account.withdraw(500));
        assertEquals(500, account.getBalance(), 0.001);
    }

    @Test
    void currentAccountAllowsConfiguredOverdraft() {
        CurrentAccount account = new CurrentAccount("CA-1", "Jordan", 100, 200);

        assertTrue(account.withdraw(250));
        assertEquals(-150, account.getBalance(), 0.001);
        assertFalse(account.withdraw(51));
    }

    @Test
    void savingsInterestIsRecordedAndApplied() {
        SavingsAccount account = new SavingsAccount("SA-2", "Taylor", 1000, 10, 0);

        account.applyInterest();

        assertEquals(1100, account.getBalance(), 0.001);
        assertEquals("INTEREST", account.getTransactions().get(1).getType());
    }

    @Test
    void invalidAccountConfigurationIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new CurrentAccount("CA-2", "Morgan", 100, -1));
        assertThrows(IllegalArgumentException.class,
                () -> new SavingsAccount("SA-3", "Morgan", 100, 2, -1));
    }
}
