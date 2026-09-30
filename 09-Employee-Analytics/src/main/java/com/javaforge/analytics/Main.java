package com.javaforge.analytics;

import com.javaforge.analytics.model.Employee;
import com.javaforge.analytics.service.EmployeeAnalyticsService;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(new Employee(111, "Jiya Brein", "HR", 25000.0, "Female", 32));
        employeeList.add(new Employee(122, "Paul Niksui", "Sales and Marketing", 13500.0, "Male", 25));
        employeeList.add(new Employee(133, "Martin Theron", "Infrastructure", 18000.0, "Male", 29));
        employeeList.add(new Employee(144, "Murali Gowda", "Product Development", 32500.0, "Male", 28));
        employeeList.add(new Employee(155, "Nima Roy", "HR", 22700.0, "Female", 27));
        employeeList.add(new Employee(166, "Iqbal Hussain", "Security And Transport", 10500.0, "Male", 43));
        employeeList.add(new Employee(177, "Manu Sharma", "Account And Finance", 27000.0, "Male", 35));
        employeeList.add(new Employee(188, "Wang Liu", "Product Development", 34500.0, "Male", 31));
        employeeList.add(new Employee(199, "Amelia Zoe", "Sales and Marketing", 11500.0, "Female", 24));
        employeeList.add(new Employee(200, "Jaden Dough", "Security And Transport", 11000.5, "Male", 38));
        employeeList.add(new Employee(211, "Jasna Kaur", "Infrastructure", 15700.0, "Female", 27));
        employeeList.add(new Employee(222, "Nitin Joshi", "Product Development", 28200.0, "Male", 25));
        employeeList.add(new Employee(233, "Jyothi Reddy", "Account And Finance", 21300.0, "Female", 27));
        employeeList.add(new Employee(244, "Nicolus Den", "Sales and Marketing", 10700.5, "Male", 24));
        employeeList.add(new Employee(255, "Ali Baig", "Infrastructure", 12700.0, "Male", 23));
        employeeList.add(new Employee(266, "Sanvi Pandey", "Product Development", 28900.0, "Female", 26));
        employeeList.add(new Employee(277, "Anuj Chettiar", "Product Development", 35700.0, "Male", 31));

        EmployeeAnalyticsService service = new EmployeeAnalyticsService();

        System.out.println("=== Employee Analytics using Java Streams ===\n");

        System.out.println("1. Group Employees by Department:");
        service.groupEmployeesByDepartment(employeeList).forEach((dept, empList) -> {
            System.out.println(dept + ": " + empList.size() + " employees");
        });
        System.out.println();

        System.out.println("2. Count Employees in each Department:");
        service.countEmployeesByDepartment(employeeList).forEach((dept, count) -> {
            System.out.println(dept + ": " + count);
        });
        System.out.println();

        System.out.println("3. Average Salary of Male and Female Employees:");
        service.averageSalaryByGender(employeeList).forEach((gender, avgSal) -> {
            System.out.printf("%s: $%.2f%n", gender, avgSal);
        });
        System.out.println();

        System.out.println("4. Highest Paid Employee:");
        service.getHighestPaidEmployee(employeeList).ifPresent(System.out::println);
        System.out.println();

        System.out.println("5. Average Age by Department:");
        service.averageAgeByDepartment(employeeList).forEach((dept, avgAge) -> {
            System.out.printf("%s: %.1f years%n", dept, avgAge);
        });
        System.out.println();

        System.out.println("6. Employees Younger Than 26:");
        System.out.println(service.getEmployeesYoungerThan(employeeList, 26));
        System.out.println();

        System.out.println("7. Total Salary of all Employees:");
        System.out.printf("$%.2f%n", service.getTotalSalary(employeeList));
        
        System.out.println("\n=== Testing Edge Cases (Empty List) ===");
        List<Employee> emptyList = new ArrayList<>();
        System.out.println("1. Group by Dept (Empty): " + service.groupEmployeesByDepartment(emptyList));
        System.out.println("2. Highest Paid (Empty): " + service.getHighestPaidEmployee(emptyList).orElse(null));
        System.out.println("3. Total Salary (Empty): $" + service.getTotalSalary(emptyList));
    }
}
