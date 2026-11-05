# 01 - Student Management

Build a **Student Management System** that allows users to manage student records from the console.

# Problem Statement

Many schools and coaching centers maintain student information manually, making it difficult to add, update, search, or remove records efficiently.

The **Student Management System** is a Java console application that provides an easy way to manage student details digitally. It enables users to store, retrieve, modify, and delete student information, reducing manual effort and improving data organization.

# Objectives

- Store student information.
- Add new students.
- View all students.
- Search for a student by ID.
- Update student details.
- Delete a student record.

# Features

- Add Student
- View All Students
- Search Student by ID
- Update Student Details
- Delete Student
- Exit

# Student Details

- Student ID
- Name
- Age
- Gender
- Department
- Course
- Marks
- Email
- Phone Number

# Tech Stack

- Java (Core Java)
- OOP (Classes & Objects)
- Array
- Scanner (User Input)
- Loops & Conditional Statements
- Methods

# Suggested Class Structure

Student
---------
id
name
age
department
marks

StudentManagement
-----------------
addStudent()
viewStudents()
searchStudent()
updateStudent()
deleteStudent()

Main
----
menu()
main()

# Sample Menu

========== Student Management System ==========

1. Add Student
2. View Students
3. Search Student
4. Update Student
5. Delete Student
6. Exit

Enter your choice:

# Sample Output

Enter Student ID: 101
Enter Name: Aakash
Enter Age: 20
Enter Department: CSE
Enter Marks: 89.5

Student added successfully!

-----------------------------
ID : 101
Name : Aakash
Age : 20
Department : CSE
Marks : 89.5
-----------------------------

# Learning Outcomes

By completing this project, you will learn:

- Creating and using classes and objects
- Constructors and methods
- Encapsulation (getters and setters)
- Working with ArrayList
- Using loops and conditional statements
- Implementing CRUD (Create, Read, Update, Delete) operations
- Building a menu-driven console application

# Project Details

Difficulty: ⭐☆☆☆☆ (Beginner)

Estimated Time: 4–8 hours

Skills Gained:
- Core Java
- OOP
- Collections
- Console-based application development

## Build and run

Requirements: JDK 21 and Maven. From this directory:

```bash
mvn clean package
java -cp target/classes com.javaforge.student.Main
```

Use the menu to add a student, list the current records, search by student ID, update a record, or delete a record. Student data is held in memory and is lost when the program exits.

## Code walkthrough

- `model/Student` represents a student record.
- `service/StudentManagement` owns the student collection and CRUD operations.
- `Main` reads console input and invokes the service.

Start with `Main.main`, follow one menu choice into `StudentManagement`, and then inspect how the service changes the collection. This is a console learning project, not a database-backed application.

## Tests

Run `mvn test` to compile and execute configured tests. This project currently has no automated test classes, so exercise every menu operation and invalid ID manually.