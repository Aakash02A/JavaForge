package com.javaforge.hospitalapi.controller;

import com.javaforge.hospitalapi.dto.PatientRequest;
import com.javaforge.hospitalapi.dto.PatientResponse;
import com.javaforge.hospitalapi.service.HospitalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final HospitalService service;

    public PatientController(HospitalService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PatientResponse> create(@Valid @RequestBody PatientRequest request) {
        PatientResponse created = service.createPatient(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<PatientResponse> findAll() {
        return service.findAllPatients();
    }

    @GetMapping("/{id}")
    public PatientResponse findById(@PathVariable long id) {
        return service.findPatient(id);
    }

    @PutMapping("/{id}")
    public PatientResponse update(@PathVariable long id, @Valid @RequestBody PatientRequest request) {
        return service.updatePatient(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
