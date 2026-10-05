package com.javaforge.hospitalapi.dto;

import com.javaforge.hospitalapi.model.Doctor;

public record DoctorSummary(Long id, String name, String specialty) {
    public static DoctorSummary from(Doctor doctor) {
        return new DoctorSummary(doctor.getId(), doctor.getName(), doctor.getSpecialty());
    }
}
