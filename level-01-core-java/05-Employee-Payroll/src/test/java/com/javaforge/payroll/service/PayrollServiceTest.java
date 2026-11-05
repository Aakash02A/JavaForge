package com.javaforge.payroll.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.javaforge.payroll.model.FullTimeEmployee;
import com.javaforge.payroll.model.PartTimeEmployee;

class PayrollServiceTest {

    @Test
    void shouldCalculateTotalPayrollForAllEmployees() {
        PayrollService payrollService = new PayrollService();

        payrollService.addEmployee(new FullTimeEmployee(101, "Alice Johnson", "Engineering", 5500.0));
        payrollService.addEmployee(new PartTimeEmployee(102, "Bob Smith", "Support", 35.0, 60));

        assertEquals(2, payrollService.getEmployees().size());
        assertEquals(7600.0, payrollService.calculateTotalPayroll(), 0.01);
    }
}
