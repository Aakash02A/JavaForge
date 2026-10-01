# Project Idea

Build an **Employee Analytics System** that performs data analysis on a list of employees using Java Streams.

# Problem Statement

Organizations often need to quickly extract insights from large employee datasets — such as finding average salaries, grouping by department, or identifying top earners. Doing this manually is slow and error-prone.

The **Employee Analytics System** is a Java application that uses the Streams API to process a collection of employee objects and produce meaningful reports in seconds.

# Objectives

- Group employees by department.
- Count employees in each department.
- Calculate average salary by gender.
- Find the highest-paid employee.
- Find average age per department.
- Filter employees by age.
- Calculate total salary expenditure.

# Features

- Group By Department
- Department Head Count
- Gender-Based Average Salary
- Highest Paid Employee Finder
- Average Age Per Department
- Age Filter
- Total Payroll Calculator

# Employee Details

- Employee ID
- Name
- Department
- Salary
- Gender
- Age

# Tech Stack

- Java (Intermediate)
- Java Streams API
- Collectors (groupingBy, counting, averagingDouble)
- Lambda Expressions
- Optional

# Suggested Class Structure

Employee
---------
id
name
department
salary
gender
age

EmployeeAnalyticsService
------------------------
groupEmployeesByDepartment()
countEmployeesByDepartment()
averageSalaryByGender()
getHighestPaidEmployee()
averageAgeByDepartment()
getEmployeesYoungerThan()
getTotalSalary()

Main
----
main()

# Sample Output

=== Employee Analytics using Java Streams ===

1. Group Employees by Department:
Product Development: 5 employees
HR: 2 employees

2. Average Salary of Male and Female Employees:
Male: $21300.09
Female: $20850.00

4. Highest Paid Employee:
Employee{id=277, name='Anuj Chettiar', salary=35700.0}

7. Total Salary of all Employees:
$359401.00

# Learning Outcomes

By completing this project, you will learn:

- Working with Java Streams and lambda expressions
- Using Collectors for grouping, counting, and averaging
- Using Optional to avoid NullPointerException
- Processing real-world data collections efficiently
- Functional-style programming in Java

# Project Details

Difficulty: ⭐⭐☆☆☆ (Intermediate)

Estimated Time: 4–6 hours

Skills Gained:
- Java Streams API
- Lambda Expressions
- Collectors
- Functional Programming
