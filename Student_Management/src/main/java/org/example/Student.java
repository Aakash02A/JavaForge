package org.example;

public class Student {
    private int id;
    private String name;
    private int age;
    private String gender;
    private String department;
    private String email;
    private String phone;

    Student(int id,String name, int age, String gender, String department, String email, String phone){
        this.id =id;
        this.name=name;
        this.age=age;
        this.gender=gender;
        this.department=department;
        this.email=email;
        this.phone=phone;
    }
    @Override
    public String toString() {
        return "ID: " + id +
                ", Name: " + name +
                ", Age: " + age +
                ", Gender: " + gender +
                ", Department: " + department +
                ", Email: " + email +
                ", Phone: " + phone;
    }
}
