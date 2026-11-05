# 20 - Railway Reservation

Build a **Railway Reservation** application that models trains, stations, and route stops, then books and looks up journeys using JDBC.

# Problem Statement

Railway booking requires combining information stored in related tables. This project demonstrates relational joins to find train routes, show station and schedule details, and retrieve complete reservation information.

# Objectives

- Connect a Java application to a relational database using JDBC.
- Model trains, stations, ordered train stops, and reservations with relational tables.
- Use SQL joins to search routes and retrieve reservation details.
- Track seat availability for overlapping route segments.
- Use JDBC transactions to validate and create reservations safely.

# Features

- Add stations, trains, and ordered train stops.
- Search trains between two stations in the correct travel direction.
- Display joined train, station, and schedule details with current seat availability.
- Book tickets and prevent overlapping journeys from exceeding capacity.
- Retrieve reservation details and cancel confirmed reservations.

# Tech Stack

- Java (Core Java, JDBC)
- Relational Database (H2 Database used for tests, swappable with MySQL/PostgreSQL)
- JUnit 5 for Testing
- SQL Joins / DAO Pattern

# Project Details

Difficulty: ⭐⭐⭐☆☆ (Intermediate)

Estimated Time: 3–5 hours

## Build and tests

Requirements: JDK 21 and Maven.

```bash
mvn clean test
```

The `RailwayReservationDAO` tests use in-memory H2 and cover schema setup, route search, bookings, seat availability, reservation lookup, and cancellation. There is no external database dependency for the test suite.

## Data model and booking flow

The schema separates `stations`, `trains`, ordered `train_stops`, and `reservations`. `findRoutes` joins a train's ordered stops and only offers destinations that occur after the origin. Availability is computed for overlapping route segments, not just by counting all reservations on a train. `bookTicket` performs its capacity check and reservation write in a JDBC transaction; cancellation changes the reservation state so it no longer consumes a seat.

Read `database` setup in `createTables`, then trace `findRoutes` and `bookTicket`. The tests are useful examples for constructing a route with multiple stops and verifying that adjacent or overlapping trips affect capacity correctly. This is a DAO exercise, not a full railway booking product.

## Build and tests

Requirements: JDK 21 and Maven.

```bash
mvn clean test
```

The `RailwayReservationDAO` tests use an in-memory H2 database and cover routes, seat availability, bookings, and reservation lookup/cancellation. No separate database server is required for tests. This project is a JDBC DAO example and does not provide a console or HTTP application.
