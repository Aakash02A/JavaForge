package com.javaforge.hospitalapi.dto;

import com.javaforge.hospitalapi.model.Doctor;

import java.time.Instant;
import java.util.List;

public record DoctorResponse(
        Long id,
        String name,
        String email,
        String specialty,
        List<PatientSummary> patients,
        Instant createdAt,
        Instant updatedAt
) {
    public static DoctorResponse from(Doctor doctor) {
        return new DoctorResponse(doctor.getId(), doctor.getName(), doctor.getEmail(), doctor.getSpecialty(),
                doctor.getPatients().stream().map(PatientSummary::from).toList(),
                doctor.getCreatedAt(), doctor.getUpdatedAt());
    }
}
