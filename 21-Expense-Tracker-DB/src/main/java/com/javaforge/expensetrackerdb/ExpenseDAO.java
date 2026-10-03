package com.javaforge.expensetrackerdb;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public ExpenseDAO(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS expenses (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "description VARCHAR(200) NOT NULL, " +
                "category VARCHAR(100) NOT NULL, " +
                "amount DECIMAL(12, 2) NOT NULL CHECK (amount > 0), " +
                "expense_date DATE NOT NULL)";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create expenses table", e);
        }
    }

    public void insert(Expense expense) {
        validate(expense);
        String sql = "INSERT INTO expenses (description, category, amount, expense_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindExpense(pstmt, expense);
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Database did not return a generated expense id");
                }
                expense.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert expense", e);
        }
    }

    public Expense findById(int id) {
        String sql = "SELECT id, description, category, amount, expense_date FROM expenses WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? mapExpense(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find expense by id", e);
        }
    }

    public List<Expense> findAll() {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT id, description, category, amount, expense_date FROM expenses ORDER BY expense_date DESC, id DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                expenses.add(mapExpense(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list expenses", e);
        }
        return expenses;
    }

    public boolean update(Expense expense) {
        validate(expense);
        if (expense.getId() <= 0) {
            throw new IllegalArgumentException("Expense id must be positive");
        }

        String sql = "UPDATE expenses SET description = ?, category = ?, amount = ?, expense_date = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            bindExpense(pstmt, expense);
            pstmt.setInt(5, expense.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update expense", e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM expenses WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete expense", e);
        }
    }

    private void bindExpense(PreparedStatement pstmt, Expense expense) throws SQLException {
        pstmt.setString(1, expense.getDescription().trim());
        pstmt.setString(2, expense.getCategory().trim());
        pstmt.setBigDecimal(3, expense.getAmount());
        pstmt.setDate(4, Date.valueOf(expense.getExpenseDate()));
    }

    private Expense mapExpense(ResultSet rs) throws SQLException {
        return new Expense(
                rs.getInt("id"),
                rs.getString("description"),
                rs.getString("category"),
                rs.getBigDecimal("amount"),
                rs.getDate("expense_date").toLocalDate()
        );
    }

    private void validate(Expense expense) {
        if (expense == null) {
            throw new IllegalArgumentException("Expense must not be null");
        }
        if (expense.getDescription() == null || expense.getDescription().isBlank()) {
            throw new IllegalArgumentException("Expense description must not be blank");
        }
        if (expense.getCategory() == null || expense.getCategory().isBlank()) {
            throw new IllegalArgumentException("Expense category must not be blank");
        }
        if (expense.getAmount() == null || expense.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Expense amount must be positive");
        }
        if (expense.getAmount().scale() > 2) {
            throw new IllegalArgumentException("Expense amount must have at most two decimal places");
        }
        if (expense.getExpenseDate() == null) {
            throw new IllegalArgumentException("Expense date must not be null");
        }
    }
}
