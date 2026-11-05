package com.javaforge.hospitalapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaforge.hospitalapi.repository.DoctorRepository;
import com.javaforge.hospitalapi.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HospitalApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
    }

    @Test
    void persistsManyToManyAssignmentsAndSupportsCrud() throws Exception {
        long doctorId = createDoctor("Dr. Maya Rao", "MAYA@hospital.example");
        long otherDoctorId = createDoctor("Dr. Sam Lee", "sam@hospital.example");
        long patientId = createPatient("Ari Kim", "ari@patient.example");
        long otherPatientId = createPatient("Nia Das", "nia@patient.example");

        mockMvc.perform(post("/api/doctors/{doctorId}/patients/{patientId}", doctorId, patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patients.length()").value(1))
                .andExpect(jsonPath("$.patients[0].id").value(patientId));
        mockMvc.perform(post("/api/doctors/{doctorId}/patients/{patientId}", doctorId, otherPatientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patients.length()").value(2));
        mockMvc.perform(post("/api/doctors/{doctorId}/patients/{patientId}", otherDoctorId, patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patients.length()").value(1));

        mockMvc.perform(get("/api/patients/{id}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctors.length()").value(2));
        mockMvc.perform(put("/api/doctors/{id}", doctorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Dr. Maya Rao","email":"maya@hospital.example","specialty":"Cardiology"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.specialty").value("Cardiology"))
                .andExpect(jsonPath("$.patients.length()").value(2));
        mockMvc.perform(delete("/api/doctors/{doctorId}/patients/{patientId}", doctorId, patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patients.length()").value(1));
        mockMvc.perform(get("/api/patients/{id}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctors.length()").value(1));
    }

    @Test
    void validatesUniqueEmailsAndRelationshipOwnership() throws Exception {
        long doctorId = createDoctor("Doctor", "doctor@hospital.example");
        long patientId = createPatient("Patient", "patient@hospital.example");

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Duplicate","email":"DOCTOR@hospital.example","specialty":"Surgery"}
                                """))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/doctors/{doctorId}/patients/{patientId}", doctorId, 900L))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/doctors/{doctorId}/patients/{patientId}", doctorId, patientId))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Invalid","email":"not-an-email","dateOfBirth":"2999-01-01"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.email").exists())
                .andExpect(jsonPath("$.validationErrors.dateOfBirth").exists());
    }

    @Test
    void deletingEitherRecordRemovesOnlyItsJoinRows() throws Exception {
        long doctorId = createDoctor("Doctor", "doctor@hospital.example");
        long otherDoctorId = createDoctor("Other Doctor", "other@hospital.example");
        long patientId = createPatient("Patient", "patient@hospital.example");
        mockMvc.perform(post("/api/doctors/{doctorId}/patients/{patientId}", doctorId, patientId))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/doctors/{doctorId}/patients/{patientId}", otherDoctorId, patientId))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/doctors/{id}", doctorId)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/patients/{id}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctors.length()").value(1));
        mockMvc.perform(delete("/api/patients/{id}", patientId)).andExpect(status().isNoContent());
        assertEquals(1, doctorRepository.count());
        assertEquals(0, patientRepository.count());
    }

    private long createDoctor(String name, String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"%s","email":"%s","specialty":"Family Medicine"}
                                """.formatted(name, email)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.get("id").asLong();
    }

    private long createPatient(String name, String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"%s","email":"%s","dateOfBirth":"1990-06-15"}
                                """.formatted(name, email)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.get("id").asLong();
    }
}
