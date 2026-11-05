package com.javaforge.bankingsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BankAccountDAO {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public BankAccountDAO(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS accounts (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "customer_name VARCHAR(100) NOT NULL, " +
                "account_number VARCHAR(50) NOT NULL UNIQUE, " +
                "balance DOUBLE NOT NULL DEFAULT 0)";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create accounts table", e);
        }
    }

    public void insert(Account account) {
        if (account == null) {
            return;
        }

        String sql = "INSERT INTO accounts (customer_name, account_number, balance) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, account.getCustomerName());
            pstmt.setString(2, account.getAccountNumber());
            pstmt.setDouble(3, account.getBalance());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    account.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert account", e);
        }
    }

    public Account findById(int id) {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapAccount(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find account by id", e);
        }
        return null;
    }

    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY id ASC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                accounts.add(mapAccount(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch all accounts", e);
        }
        return accounts;
    }

    public boolean deposit(int accountId, double amount) {
        if (amount <= 0) {
            return false;
        }

        String sql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to deposit into account", e);
        }
    }

    public boolean withdraw(int accountId, double amount) {
        if (amount <= 0) {
            return false;
        }

        String sql = "UPDATE accounts SET balance = balance - ? WHERE id = ? AND balance >= ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountId);
            pstmt.setDouble(3, amount);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to withdraw from account", e);
        }
    }

    public boolean transfer(int fromAccountId, int toAccountId, double amount) {
        if (fromAccountId == toAccountId || amount <= 0) {
            return false;
        }

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                Account source = findById(conn, fromAccountId);
                Account target = findById(conn, toAccountId);

                if (source == null || target == null || source.getBalance() < amount) {
                    conn.rollback();
                    return false;
                }

                boolean debitSucceeded = debit(conn, fromAccountId, amount);
                boolean creditSucceeded = credit(conn, toAccountId, amount);

                if (!debitSucceeded || !creditSucceeded) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Transfer failed", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to transfer funds", e);
        }
    }

    private Account findById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapAccount(rs);
                }
            }
        }
        return null;
    }

    private boolean debit(Connection conn, int accountId, double amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance - ? WHERE id = ? AND balance >= ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountId);
            pstmt.setDouble(3, amount);
            return pstmt.executeUpdate() > 0;
        }
    }

    private boolean credit(Connection conn, int accountId, double amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Account mapAccount(ResultSet rs) throws SQLException {
        return new Account(
                rs.getInt("id"),
                rs.getString("customer_name"),
                rs.getString("account_number"),
                rs.getDouble("balance")
        );
    }
}
