package com.javaforge.analytics.service;

import com.javaforge.analytics.model.Employee;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmployeeAnalyticsService {

    // 1. Group Employees by Department
    public Map<String, List<Employee>> groupEmployeesByDepartment(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));
    }

    // 2. Count Employees in each Department
    public Map<String, Long> countEmployeesByDepartment(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));
    }

    // 3. Average Salary of Male and Female Employees
    public Map<String, Double> averageSalaryByGender(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(Employee::getGender, Collectors.averagingDouble(Employee::getSalary)));
    }

    // 4. Get the Highest Paid Employee
    public Optional<Employee> getHighestPaidEmployee(List<Employee> employees) {
        return employees.stream()
                .max(Comparator.comparingDouble(Employee::getSalary));
    }

    // 5. Get Average Age of each Department
    public Map<String, Double> averageAgeByDepartment(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.averagingInt(Employee::getAge)));
    }

    // 6. Get names of all employees who have joined recently (or sort by age as proxy here)
    // Let's filter out employees older than a specific age
    public List<String> getEmployeesYoungerThan(List<Employee> employees, int age) {
        return employees.stream()
                .filter(e -> e.getAge() < age)
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    // 7. Get total sum of salaries across the company
    public double getTotalSalary(List<Employee> employees) {
        return employees.stream()
                .mapToDouble(Employee::getSalary)
                .sum();
    }
}
