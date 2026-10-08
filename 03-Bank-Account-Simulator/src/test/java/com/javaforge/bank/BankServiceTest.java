package com.javaforge.bank;

import com.javaforge.bank.model.CurrentAccount;
import com.javaforge.bank.service.BankService;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankServiceTest {
    @Test
    void transferMovesMoneyAndCreatesOneEntryPerAccount() {
        BankService service = new BankService(new Scanner(""), false);
        CurrentAccount source = new CurrentAccount("CA-10", "Source", 500, 0);
        CurrentAccount destination = new CurrentAccount("CA-11", "Destination", 100, 0);
        service.addAccount(source);
        service.addAccount(destination);

        assertTrue(service.transfer("CA-10", "CA-11", 150));

        assertEquals(350, source.getBalance(), 0.001);
        assertEquals(250, destination.getBalance(), 0.001);
        assertEquals(2, source.getTransactions().size());
        assertEquals(2, destination.getTransactions().size());
    }

    @Test
    void transferRejectsUnknownOrSameAccount() {
        BankService service = new BankService(new Scanner(""), false);
        service.addAccount(new CurrentAccount("CA-12", "Source", 500, 0));

        assertFalse(service.transfer("CA-12", "missing", 50));
        assertFalse(service.transfer("CA-12", "CA-12", 50));
    }
}
