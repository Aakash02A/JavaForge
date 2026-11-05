package com.javaforge.bankingsystem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankAccountDAOTest {
    private BankAccountDAO dao;
    private final String url = "jdbc:h2:mem:bankingdb;DB_CLOSE_DELAY=-1";
    private final String user = "sa";
    private final String password = "";

    @BeforeEach
    void setUp() {
        dao = new BankAccountDAO(url, user, password);
        dao.createTable();
    }

    @AfterEach
    void tearDown() throws Exception {
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE accounts");
        }
    }

    @Test
    void testInsertAndFindById() {
        Account account = new Account("Alice", "ACC-1001", 500.0);
        dao.insert(account);

        assertTrue(account.getId() > 0);

        Account found = dao.findById(account.getId());
        assertNotNull(found);
        assertEquals("Alice", found.getCustomerName());
        assertEquals("ACC-1001", found.getAccountNumber());
        assertEquals(500.0, found.getBalance(), 0.001);
    }

    @Test
    void testDepositAndWithdraw() {
        Account account = new Account("Bob", "ACC-2002", 250.0);
        dao.insert(account);

        assertTrue(dao.deposit(account.getId(), 100.0));
        assertEquals(350.0, dao.findById(account.getId()).getBalance(), 0.001);

        assertTrue(dao.withdraw(account.getId(), 75.0));
        assertEquals(275.0, dao.findById(account.getId()).getBalance(), 0.001);

        assertFalse(dao.withdraw(account.getId(), 500.0));
        assertEquals(275.0, dao.findById(account.getId()).getBalance(), 0.001);
    }

    @Test
    void testTransferSuccess() {
        Account source = new Account("Carol", "ACC-3003", 1000.0);
        Account target = new Account("Dave", "ACC-4004", 300.0);
        dao.insert(source);
        dao.insert(target);

        assertTrue(dao.transfer(source.getId(), target.getId(), 250.0));
        assertEquals(750.0, dao.findById(source.getId()).getBalance(), 0.001);
        assertEquals(550.0, dao.findById(target.getId()).getBalance(), 0.001);
    }

    @Test
    void testTransferFailsWhenInsufficientFunds() {
        Account source = new Account("Eve", "ACC-5005", 100.0);
        Account target = new Account("Frank", "ACC-6006", 250.0);
        dao.insert(source);
        dao.insert(target);

        assertFalse(dao.transfer(source.getId(), target.getId(), 250.0));
        assertEquals(100.0, dao.findById(source.getId()).getBalance(), 0.001);
        assertEquals(250.0, dao.findById(target.getId()).getBalance(), 0.001);
    }
}
