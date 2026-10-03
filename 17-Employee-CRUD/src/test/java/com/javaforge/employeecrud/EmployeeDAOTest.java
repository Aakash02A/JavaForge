package com.javaforge.employeecrud;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeDAOTest {
    private EmployeeDAO dao;
    private final String url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private final String user = "sa";
    private final String password = "";

    @BeforeEach
    void setUp() throws Exception {
        dao = new EmployeeDAO(url, user, password);
        dao.createTable();
    }

    @AfterEach
    void tearDown() throws Exception {
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE employees");
        }
    }

    @Test
    void testInsertAndFindById() {
        Employee emp = new Employee("Alice", "Engineering", 75000);
        dao.insert(emp);
        assertTrue(emp.getId() > 0);

        Employee fetched = dao.findById(emp.getId());
        assertNotNull(fetched);
        assertEquals("Alice", fetched.getName());
        assertEquals("Engineering", fetched.getDepartment());
        assertEquals(75000, fetched.getSalary());
    }

    @Test
    void testUpdate() {
        Employee emp = new Employee("Bob", "HR", 50000);
        dao.insert(emp);
        
        emp.setSalary(55000);
        emp.setDepartment("Finance");
        boolean updated = dao.update(emp);
        assertTrue(updated);
        
        Employee fetched = dao.findById(emp.getId());
        assertEquals(55000, fetched.getSalary());
        assertEquals("Finance", fetched.getDepartment());
    }

    @Test
    void testDelete() {
        Employee emp = new Employee("Charlie", "Sales", 60000);
        dao.insert(emp);
        
        boolean deleted = dao.delete(emp.getId());
        assertTrue(deleted);
        
        assertNull(dao.findById(emp.getId()));
    }

    @Test
    void testFindAll() {
        dao.insert(new Employee("Emp1", "IT", 40000));
        dao.insert(new Employee("Emp2", "IT", 45000));
        
        List<Employee> all = dao.findAll();
        assertEquals(2, all.size());
    }
}
