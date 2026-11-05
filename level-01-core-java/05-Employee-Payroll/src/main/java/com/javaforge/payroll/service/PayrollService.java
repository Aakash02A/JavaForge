package com.javaforge.payroll.service;

import java.util.ArrayList;
import java.util.List;

import com.javaforge.payroll.model.Employee;

public class PayrollService {
    private final List<Employee> employees = new ArrayList<>();

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public Employee removeEmployee(int employeeId) {
        for (Employee employee : employees) {
            if (employee.getId() == employeeId) {
                employees.remove(employee);
                return employee;
            }
        }
        return null;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public double calculateTotalPayroll() {
        double total = 0;
        for (Employee employee : employees) {
            total += employee.calculateSalary();
        }
        return total;
    }

    public void viewEmployees() {
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        System.out.println("\n===== Employees =====");
        for (Employee employee : employees) {
            System.out.println(employee);
        }
    }

    public void viewPayrollSummary() {
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        System.out.println("\n===== Payroll Summary =====");
        for (Employee employee : employees) {
            System.out.printf("%s (%s) -> $%.2f%n",
                    employee.getName(),
                    employee.getDepartment(),
                    employee.calculateSalary());
        }
        System.out.printf("Total Payroll: $%.2f%n", calculateTotalPayroll());
    }
}
