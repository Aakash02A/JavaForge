package com.javaforge.railwayreservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RailwayReservationDAO {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public RailwayReservationDAO(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public void createTables() {
        String[] statements = {
                "CREATE TABLE IF NOT EXISTS stations (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "station_code VARCHAR(10) NOT NULL UNIQUE, " +
                        "name VARCHAR(100) NOT NULL)",
                "CREATE TABLE IF NOT EXISTS trains (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "train_number VARCHAR(20) NOT NULL UNIQUE, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "capacity INT NOT NULL CHECK (capacity > 0))",
                "CREATE TABLE IF NOT EXISTS train_stops (" +
                        "train_id INT NOT NULL REFERENCES trains(id), " +
                        "station_id INT NOT NULL REFERENCES stations(id), " +
                        "sequence_no INT NOT NULL CHECK (sequence_no > 0), " +
                        "arrival_time TIME, " +
                        "departure_time TIME, " +
                        "PRIMARY KEY (train_id, sequence_no), " +
                        "UNIQUE (train_id, station_id))",
                "CREATE TABLE IF NOT EXISTS reservations (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "customer_name VARCHAR(100) NOT NULL, " +
                        "train_id INT NOT NULL REFERENCES trains(id), " +
                        "from_station_id INT NOT NULL REFERENCES stations(id), " +
                        "to_station_id INT NOT NULL REFERENCES stations(id), " +
                        "status VARCHAR(20) NOT NULL CHECK (status IN ('CONFIRMED', 'CANCELLED')), " +
                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)"
        };

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            for (String sql : statements) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create railway reservation tables", e);
        }
    }

    public int addStation(String stationCode, String name) {
        requireText(stationCode, "Station code");
        requireText(name, "Station name");
        return insertAndGetId(
                "INSERT INTO stations (station_code, name) VALUES (?, ?)",
                stationCode, name
        );
    }

    public int addTrain(String trainNumber, String name, int capacity) {
        requireText(trainNumber, "Train number");
        requireText(name, "Train name");
        if (capacity <= 0) {
            throw new IllegalArgumentException("Train capacity must be positive");
        }

        return insertAndGetId(
                "INSERT INTO trains (train_number, name, capacity) VALUES (?, ?, ?)",
                trainNumber, name, capacity
        );
    }

    public void addStop(int trainId, int stationId, int sequence,
                        LocalTime arrivalTime, LocalTime departureTime) {
        if (sequence <= 0) {
            throw new IllegalArgumentException("Stop sequence must be positive");
        }
        String sql = "INSERT INTO train_stops (train_id, station_id, sequence_no, arrival_time, departure_time) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, trainId);
            pstmt.setInt(2, stationId);
            pstmt.setInt(3, sequence);
            pstmt.setTime(4, toSqlTime(arrivalTime));
            pstmt.setTime(5, toSqlTime(departureTime));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add train stop", e);
        }
    }

    public List<TrainRoute> findRoutes(String fromStationCode, String toStationCode) {
        requireText(fromStationCode, "Origin station code");
        requireText(toStationCode, "Destination station code");

        String sql = "SELECT t.id AS train_id, t.train_number, t.name AS train_name, " +
                "from_station.station_code AS from_code, from_station.name AS from_name, " +
                "from_stop.departure_time, to_station.station_code AS to_code, " +
                "to_station.name AS to_name, to_stop.arrival_time, " +
                "t.capacity - (" +
                "SELECT COUNT(*) FROM reservations r " +
                "JOIN train_stops reserved_from ON reserved_from.train_id = r.train_id " +
                "AND reserved_from.station_id = r.from_station_id " +
                "JOIN train_stops reserved_to ON reserved_to.train_id = r.train_id " +
                "AND reserved_to.station_id = r.to_station_id " +
                "WHERE r.train_id = t.id AND r.status = 'CONFIRMED' " +
                "AND reserved_from.sequence_no < to_stop.sequence_no " +
                "AND reserved_to.sequence_no > from_stop.sequence_no) AS available_seats " +
                "FROM trains t " +
                "JOIN train_stops from_stop ON from_stop.train_id = t.id " +
                "JOIN stations from_station ON from_station.id = from_stop.station_id " +
                "JOIN train_stops to_stop ON to_stop.train_id = t.id " +
                "AND from_stop.sequence_no < to_stop.sequence_no " +
                "JOIN stations to_station ON to_station.id = to_stop.station_id " +
                "WHERE from_station.station_code = ? AND to_station.station_code = ? " +
                "ORDER BY t.train_number";

        List<TrainRoute> routes = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fromStationCode);
            pstmt.setString(2, toStationCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    routes.add(new TrainRoute(
                            rs.getInt("train_id"),
                            rs.getString("train_number"),
                            rs.getString("train_name"),
                            rs.getString("from_code"),
                            rs.getString("from_name"),
                            toLocalTime(rs.getTime("departure_time")),
                            rs.getString("to_code"),
                            rs.getString("to_name"),
                            toLocalTime(rs.getTime("arrival_time")),
                            rs.getInt("available_seats")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find train routes", e);
        }
        return routes;
    }

    public Reservation bookTicket(String customerName, int trainId,
                                  int fromStationId, int toStationId) {
        requireText(customerName, "Customer name");
        if (fromStationId == toStationId) {
            throw new IllegalArgumentException("Origin and destination must be different");
        }

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                int capacity = lockAndGetCapacity(conn, trainId);
                int fromSequence = findStopSequence(conn, trainId, fromStationId);
                int toSequence = findStopSequence(conn, trainId, toStationId);
                if (fromSequence >= toSequence) {
                    throw new IllegalArgumentException("Destination must follow origin on the train route");
                }
                if (countOverlappingReservations(conn, trainId, fromSequence, toSequence) >= capacity) {
                    throw new IllegalStateException("No seats are available for this journey");
                }

                int reservationId = insertReservation(conn, customerName, trainId, fromStationId, toStationId);
                Reservation reservation = findReservation(conn, reservationId);
                conn.commit();
                return reservation;
            } catch (SQLException | RuntimeException e) {
                rollback(conn, e);
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to book ticket", e);
        }
    }

    public Reservation findReservationById(int reservationId) {
        try (Connection conn = getConnection()) {
            return findReservation(conn, reservationId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find reservation", e);
        }
    }

    public boolean cancelReservation(int reservationId) {
        String sql = "UPDATE reservations SET status = 'CANCELLED' WHERE id = ? AND status = 'CONFIRMED'";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to cancel reservation", e);
        }
    }

    private int insertAndGetId(String sql, String firstValue, String secondValue) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, firstValue);
            pstmt.setString(2, secondValue);
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new SQLException("Database did not return a generated id");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save railway record", e);
        }
    }

    private int insertAndGetId(String sql, String firstValue, String secondValue, int thirdValue) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, firstValue);
            pstmt.setString(2, secondValue);
            pstmt.setInt(3, thirdValue);
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new SQLException("Database did not return a generated id");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save railway record", e);
        }
    }

    private int lockAndGetCapacity(Connection conn, int trainId) throws SQLException {
        String sql = "SELECT capacity FROM trains WHERE id = ? FOR UPDATE";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, trainId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Train does not exist: " + trainId);
                }
                return rs.getInt("capacity");
            }
        }
    }

    private int findStopSequence(Connection conn, int trainId, int stationId) throws SQLException {
        String sql = "SELECT sequence_no FROM train_stops WHERE train_id = ? AND station_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, trainId);
            pstmt.setInt(2, stationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Station is not on the selected train route");
                }
                return rs.getInt("sequence_no");
            }
        }
    }

    private int countOverlappingReservations(Connection conn, int trainId,
                                             int fromSequence, int toSequence) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations r " +
                "JOIN train_stops reserved_from ON reserved_from.train_id = r.train_id " +
                "AND reserved_from.station_id = r.from_station_id " +
                "JOIN train_stops reserved_to ON reserved_to.train_id = r.train_id " +
                "AND reserved_to.station_id = r.to_station_id " +
                "WHERE r.train_id = ? AND r.status = 'CONFIRMED' " +
                "AND reserved_from.sequence_no < ? AND reserved_to.sequence_no > ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, trainId);
            pstmt.setInt(2, toSequence);
            pstmt.setInt(3, fromSequence);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private int insertReservation(Connection conn, String customerName, int trainId,
                                  int fromStationId, int toStationId) throws SQLException {
        String sql = "INSERT INTO reservations " +
                "(customer_name, train_id, from_station_id, to_station_id, status) " +
                "VALUES (?, ?, ?, ?, 'CONFIRMED')";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, customerName);
            pstmt.setInt(2, trainId);
            pstmt.setInt(3, fromStationId);
            pstmt.setInt(4, toStationId);
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new SQLException("Database did not return a reservation id");
        }
    }

    private Reservation findReservation(Connection conn, int reservationId) throws SQLException {
        String sql = "SELECT r.id, r.customer_name, r.status, t.train_number, t.name AS train_name, " +
                "from_station.station_code AS from_code, from_station.name AS from_name, " +
                "from_stop.departure_time, to_station.station_code AS to_code, " +
                "to_station.name AS to_name, to_stop.arrival_time " +
                "FROM reservations r " +
                "JOIN trains t ON t.id = r.train_id " +
                "JOIN stations from_station ON from_station.id = r.from_station_id " +
                "JOIN stations to_station ON to_station.id = r.to_station_id " +
                "JOIN train_stops from_stop ON from_stop.train_id = r.train_id " +
                "AND from_stop.station_id = r.from_station_id " +
                "JOIN train_stops to_stop ON to_stop.train_id = r.train_id " +
                "AND to_stop.station_id = r.to_station_id WHERE r.id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Reservation(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        rs.getString("train_number"),
                        rs.getString("train_name"),
                        rs.getString("from_code"),
                        rs.getString("from_name"),
                        toLocalTime(rs.getTime("departure_time")),
                        rs.getString("to_code"),
                        rs.getString("to_name"),
                        toLocalTime(rs.getTime("arrival_time")),
                        rs.getString("status")
                );
            }
        }
    }

    private static Time toSqlTime(LocalTime time) {
        return time == null ? null : Time.valueOf(time);
    }

    private static LocalTime toLocalTime(Time time) {
        return time == null ? null : time.toLocalTime();
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }

    private static void rollback(Connection conn, Exception originalException) {
        try {
            conn.rollback();
        } catch (SQLException rollbackException) {
            originalException.addSuppressed(rollbackException);
        }
    }
}
