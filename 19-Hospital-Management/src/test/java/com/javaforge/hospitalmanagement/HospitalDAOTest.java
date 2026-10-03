package com.javaforge.hospitalmanagement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HospitalDAOTest {
    private HospitalDAO dao;
    private final String url = "jdbc:h2:mem:hospitaldb;DB_CLOSE_DELAY=-1";
    private final String user = "sa";
    private final String password = "";

    @BeforeEach
    void setUp() {
        dao = new HospitalDAO(url, user, password);
        dao.createTable();
    }

    @AfterEach
    void tearDown() throws Exception {
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE patients");
        }
    }

    @Test
    void testInsertAndFindById() {
        Patient patient = new Patient("Alice Johnson", 32, "Cardiology", "Dr. Shah", "Admitted");
        dao.insert(patient);

        assertTrue(patient.getId() > 0);

        Patient found = dao.findById(patient.getId());
        assertNotNull(found);
        assertEquals("Alice Johnson", found.getName());
        assertEquals("Cardiology", found.getDepartment());
        assertEquals("Dr. Shah", found.getDoctorName());
        assertEquals("Admitted", found.getStatus());
    }

    @Test
    void testFindByDepartment() {
        dao.insert(new Patient("Bob Smith", 41, "Orthopedics", "Dr. Mehta", "Under Observation"));
        dao.insert(new Patient("Carol White", 30, "Orthopedics", "Dr. Mehta", "Discharged"));
        dao.insert(new Patient("David Lee", 56, "Neurology", "Dr. Singh", "Admitted"));

        List<Patient> orthopedicPatients = dao.findByDepartment("Orthopedics");
        assertEquals(2, orthopedicPatients.size());
    }

    @Test
    void testUpdateStatus() {
        Patient patient = new Patient("Emma Green", 27, "Dermatology", "Dr. Rao", "Waiting");
        dao.insert(patient);

        assertTrue(dao.updateStatus(patient.getId(), "Under Treatment"));
        assertEquals("Under Treatment", dao.findById(patient.getId()).getStatus());
    }

    @Test
    void testDelete() {
        Patient patient = new Patient("Frank Brown", 52, "Emergency", "Dr. Kumar", "Critical");
        dao.insert(patient);

        assertTrue(dao.delete(patient.getId()));
        assertNull(dao.findById(patient.getId()));
        assertFalse(dao.delete(patient.getId()));
    }
}
