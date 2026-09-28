package com.javaforge.payroll.model;

public class PartTimeEmployee extends Employee {
    private final double hourlyRate;
    private final int hoursWorked;

    public PartTimeEmployee(int id, String name, String department, double hourlyRate, int hoursWorked) {
        super(id, name, department);
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    @Override
    public double calculateSalary() {
        return hourlyRate * hoursWorked;
    }
}
