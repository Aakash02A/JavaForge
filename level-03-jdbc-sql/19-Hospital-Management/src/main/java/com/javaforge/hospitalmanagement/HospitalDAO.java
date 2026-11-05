package com.javaforge.hospitalmanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HospitalDAO {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public HospitalDAO(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS patients (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "age INT NOT NULL, " +
                "department VARCHAR(100) NOT NULL, " +
                "doctor_name VARCHAR(100) NOT NULL, " +
                "status VARCHAR(50) NOT NULL)";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create patients table", e);
        }
    }

    public void insert(Patient patient) {
        if (patient == null) {
            return;
        }

        String sql = "INSERT INTO patients (name, age, department, doctor_name, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, patient.getName());
            pstmt.setInt(2, patient.getAge());
            pstmt.setString(3, patient.getDepartment());
            pstmt.setString(4, patient.getDoctorName());
            pstmt.setString(5, patient.getStatus());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    patient.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert patient", e);
        }
    }

    public Patient findById(int id) {
        String sql = "SELECT * FROM patients WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapPatient(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find patient by id", e);
        }
        return null;
    }

    public List<Patient> findAll() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY id ASC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                patients.add(mapPatient(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list patients", e);
        }
        return patients;
    }

    public List<Patient> findByDepartment(String department) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE department = ? ORDER BY id ASC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, department);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(mapPatient(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find patients by department", e);
        }
        return patients;
    }

    public boolean updateStatus(int id, String newStatus) {
        String sql = "UPDATE patients SET status = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update patient status", e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM patients WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete patient", e);
        }
    }

    private Patient mapPatient(ResultSet rs) throws SQLException {
        return new Patient(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("age"),
                rs.getString("department"),
                rs.getString("doctor_name"),
                rs.getString("status")
        );
    }
}
