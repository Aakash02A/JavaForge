package com.javaforge.hospitalmanagement;

import java.util.Objects;

public class Patient {
    private int id;
    private String name;
    private int age;
    private String department;
    private String doctorName;
    private String status;

    public Patient() {
    }

    public Patient(String name, int age, String department, String doctorName, String status) {
        this.name = name;
        this.age = age;
        this.department = department;
        this.doctorName = doctorName;
        this.status = status;
    }

    public Patient(int id, String name, int age, String department, String doctorName, String status) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.department = department;
        this.doctorName = doctorName;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return id == patient.id && age == patient.age &&
                Objects.equals(name, patient.name) &&
                Objects.equals(department, patient.department) &&
                Objects.equals(doctorName, patient.doctorName) &&
                Objects.equals(status, patient.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, department, doctorName, status);
    }

    @Override
    public String toString() {
        return "Patient{id=" + id + ", name='" + name + "', age=" + age + ", department='" + department + "', doctorName='" + doctorName + "', status='" + status + "'}";
    }
}
