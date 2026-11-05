# 05 - Employee Payroll

A Java console application for managing full-time and part-time employees and calculating payroll. It demonstrates interfaces, inheritance, and polymorphism through a shared `Employee` model with different salary calculations.

## Features

- Add full-time employees with a monthly salary.
- Add part-time employees with an hourly rate and hours worked.
- List employees and view a per-employee and total payroll summary.
- Remove an employee by ID.
- Validate menu and numeric input.

## Design

- `Employee` defines common employee data and the salary-calculation contract.
- `FullTimeEmployee` and `PartTimeEmployee` provide the respective calculation rules.
- `PayrollService` stores employees in memory and performs list, removal, and payroll operations.
- `Main` implements the interactive `Scanner` menu.

All data exists only for the current run. Payroll uses Java `double`, so this is a learning example rather than financial software.

## Requirements and run

- JDK 21
- Maven

From this directory:

```bash
mvn clean test
mvn package
```

Run `com.javaforge.payroll.Main` from your IDE, or use the compiled class:

```bash
java -cp target/classes com.javaforge.payroll.Main
```

Choose `1` or `2` to add an employee, `3` to list employees, `4` to view payroll, and `5` to remove an employee. Enter `6` to exit.

## Tests

Run `mvn test`. The unit tests cover the payroll service; the menu can also be exercised manually using the steps above.
