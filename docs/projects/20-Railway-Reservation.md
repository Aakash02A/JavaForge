# Project Idea

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
