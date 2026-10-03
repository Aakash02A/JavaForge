package com.javaforge.railwayreservation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RailwayReservationDAOTest {
    private RailwayReservationDAO dao;
    private final String url = "jdbc:h2:mem:railwaydb;DB_CLOSE_DELAY=-1";
    private int trainId;
    private int stationA;
    private int stationB;
    private int stationC;

    @BeforeEach
    void setUp() {
        dao = new RailwayReservationDAO(url, "sa", "");
        dao.createTables();

        stationA = dao.addStation("AAA", "Alpha");
        stationB = dao.addStation("BBB", "Bravo");
        stationC = dao.addStation("CCC", "Charlie");
        trainId = dao.addTrain("XF100", "Express", 1);
        dao.addStop(trainId, stationA, 1, null, LocalTime.of(8, 0));
        dao.addStop(trainId, stationB, 2, LocalTime.of(9, 0), LocalTime.of(9, 5));
        dao.addStop(trainId, stationC, 3, LocalTime.of(10, 0), null);
    }

    @AfterEach
    void tearDown() throws Exception {
        try (Connection conn = DriverManager.getConnection(url, "sa", "");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE reservations");
            stmt.execute("DROP TABLE train_stops");
            stmt.execute("DROP TABLE trains");
            stmt.execute("DROP TABLE stations");
        }
    }

    @Test
    void findsRouteByJoiningTrainStopsAndStations() {
        List<TrainRoute> routes = dao.findRoutes("AAA", "CCC");

        assertEquals(1, routes.size());
        assertEquals("XF100", routes.get(0).getTrainNumber());
        assertEquals("Alpha", routes.get(0).getFromStationName());
        assertEquals("Charlie", routes.get(0).getToStationName());
        assertEquals(LocalTime.of(8, 0), routes.get(0).getDepartureTime());
        assertEquals(1, routes.get(0).getAvailableSeats());
        assertTrue(dao.findRoutes("CCC", "AAA").isEmpty());
    }

    @Test
    void booksAndRetrievesReservationUsingJoinedDetails() {
        Reservation booked = dao.bookTicket("Alice", trainId, stationA, stationC);
        Reservation found = dao.findReservationById(booked.getId());

        assertNotNull(found);
        assertEquals("Alice", found.getCustomerName());
        assertEquals("XF100", found.getTrainNumber());
        assertEquals("Alpha", found.getFromStationName());
        assertEquals("Charlie", found.getToStationName());
        assertEquals("CONFIRMED", found.getStatus());
        assertEquals(0, dao.findRoutes("AAA", "CCC").get(0).getAvailableSeats());
    }

    @Test
    void rejectsOverlappingBookingWhenTrainSegmentIsFull() {
        dao.bookTicket("Alice", trainId, stationA, stationB);

        assertThrows(IllegalStateException.class,
                () -> dao.bookTicket("Bob", trainId, stationA, stationC));
    }

    @Test
    void allowsJourneyOnNonOverlappingSegmentAndReleasesCancelledSeats() {
        Reservation firstJourney = dao.bookTicket("Alice", trainId, stationA, stationB);

        Reservation secondJourney = dao.bookTicket("Bob", trainId, stationB, stationC);
        assertNotNull(secondJourney);

        assertTrue(dao.cancelReservation(firstJourney.getId()));
        assertFalse(dao.cancelReservation(firstJourney.getId()));
        assertEquals(1, dao.findRoutes("AAA", "BBB").get(0).getAvailableSeats());
    }

    @Test
    void rejectsJourneyInReverseOrder() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.bookTicket("Alice", trainId, stationC, stationA));
    }
}
