package com.javaforge.payroll;

import java.util.InputMismatchException;
import java.util.Scanner;

import com.javaforge.payroll.model.Employee;
import com.javaforge.payroll.model.FullTimeEmployee;
import com.javaforge.payroll.model.PartTimeEmployee;
import com.javaforge.payroll.service.PayrollService;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final PayrollService payrollService = new PayrollService();

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\n===== Employee Payroll =====");
            System.out.println("1. Add Full-Time Employee");
            System.out.println("2. Add Part-Time Employee");
            System.out.println("3. View All Employees");
            System.out.println("4. View Payroll Summary");
            System.out.println("5. Remove Employee");
            System.out.println("6. Exit");
            System.out.print("Enter Choice: ");

            try {
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> addFullTimeEmployee();
                    case 2 -> addPartTimeEmployee();
                    case 3 -> payrollService.viewEmployees();
                    case 4 -> payrollService.viewPayrollSummary();
                    case 5 -> removeEmployee();
                    case 6 -> {
                        System.out.println("Exiting Employee Payroll. Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                sc.nextLine();
            }
        }
    }

    private static void addFullTimeEmployee() {
        System.out.println("===== Add Full-Time Employee =====");
        int id = readInt("Enter employee ID: ");
        String name = readString("Enter employee name: ");
        String department = readString("Enter department: ");
        double salary = readPositiveDouble("Enter monthly salary: ");

        payrollService.addEmployee(new FullTimeEmployee(id, name, department, salary));
        System.out.println("Full-time employee added successfully.");
    }

    private static void addPartTimeEmployee() {
        System.out.println("===== Add Part-Time Employee =====");
        int id = readInt("Enter employee ID: ");
        String name = readString("Enter employee name: ");
        String department = readString("Enter department: ");
        double hourlyRate = readPositiveDouble("Enter hourly rate: ");
        int hoursWorked = readPositiveInt("Enter hours worked: ");

        payrollService.addEmployee(new PartTimeEmployee(id, name, department, hourlyRate, hoursWorked));
        System.out.println("Part-time employee added successfully.");
    }

    private static void removeEmployee() {
        int id = readInt("Enter employee ID to remove: ");
        Employee removed = payrollService.removeEmployee(id);

        if (removed != null) {
            System.out.println("Employee removed successfully: " + removed.getName());
        } else {
            System.out.println("Employee with ID " + id + " not found.");
        }
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private static int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Value must be greater than 0.");
        }
    }

    private static double readPositiveDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = Double.parseDouble(sc.nextLine());
                if (value > 0) {
                    return value;
                }
                System.out.println("Value must be greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
