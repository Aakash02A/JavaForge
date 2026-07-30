package org.example;

import java.util.ArrayList;
import java.util.Scanner;

class Student {
    private int id;
    private String name;
    private int age;
    private String gender;
    private String department;
    private String course;
    private double marks;
    private String email;
    private String phone;

    Student(int id,String name, int age, String gender, String department, String course, double marks, String email, String phone){
        this.id =id;
        this.name=name;
        this.age=age;
        this.gender=gender;
        this.department=department;
        this.course=course;
        this.marks=marks;
        this.email=email;
        this.phone=phone;
    }

}



public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Student s = new Student();


    }
}