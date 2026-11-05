package com.javaforge.employeemanagement.service;

import com.javaforge.employeemanagement.dto.EmployeeRequest;
import com.javaforge.employeemanagement.dto.EmployeeResponse;
import com.javaforge.employeemanagement.exception.DuplicateEmployeeEmailException;
import com.javaforge.employeemanagement.exception.EmployeeNotFoundException;
import com.javaforge.employeemanagement.model.Employee;
import com.javaforge.employeemanagement.repository.EmployeeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public EmployeeResponse create(EmployeeRequest request) {
        String email = normalizeEmail(request.email());
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmployeeEmailException(email);
        }
        Employee employee = new Employee(request.firstName().trim(), request.lastName().trim(), email,
                request.department().trim(), request.jobTitle().trim(), request.salary(), request.hireDate());
        return EmployeeResponse.from(repository.save(employee));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(EmployeeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findById(long id) {
        return EmployeeResponse.from(getEmployee(id));
    }

    public EmployeeResponse update(long id, EmployeeRequest request) {
        Employee employee = getEmployee(id);
        String email = normalizeEmail(request.email());
        if (repository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateEmployeeEmailException(email);
        }
        employee.updateDetails(request.firstName().trim(), request.lastName().trim(), email,
                request.department().trim(), request.jobTitle().trim(), request.salary(), request.hireDate());
        return EmployeeResponse.from(repository.save(employee));
    }

    public void delete(long id) {
        Employee employee = getEmployee(id);
        repository.delete(employee);
    }

    private Employee getEmployee(long id) {
        return repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
