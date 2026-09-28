# 01 - Student Management

A console-based student record management application built with Core Java, focusing on **Classes & Objects** and **CRUD Operations**.

## 🚀 Features

- **Add Student:** Register new students with ID validation.
- **View All Students:** List all enrolled students with detailed information.
- **Search Student:** Find students quickly by their ID.
- **Update Student:** Update all details or selectively modify individual fields.
- **Delete Student:** Remove student records by ID.

## 🛠️ Tech Stack & Concepts

- **Language:** Java 21
- **Concepts:** Classes & Objects, Encapsulation, Collections (`ArrayList`), Scanner I/O
- **Architecture:** Layered packaging (`model`, `service`, `Main`)

## 📂 Project Structure

```
01-Student-Management/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── javaforge/
                    └── student/
                        ├── model/
                        │   └── Student.java
                        ├── service/
                        │   └── StudentManagement.java
                        └── Main.java
```

## ▶️ How to Run

Compile and execute:

```bash
cd 01-Student-Management
javac -d target/classes src/main/java/com/javaforge/student/model/*.java src/main/java/com/javaforge/student/service/*.java src/main/java/com/javaforge/student/*.java
java -cp target/classes com.javaforge.student.Main
```
