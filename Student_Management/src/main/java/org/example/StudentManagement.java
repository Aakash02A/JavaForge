package org.example;

import java.util.ArrayList;
import java.util.Scanner;

public class StudentManagement {
    private ArrayList<Student> students = new ArrayList<>();

    private Scanner sc = new Scanner(System.in);


    public void addStudent() {

        System.out.print("Enter your ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter your Name: ");
        String name = sc.nextLine();
        System.out.print("Enter your Age: ");
        int age = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter your Gender: ");
        String gender = sc.nextLine();
        System.out.print("Enter your Department: ");
        String department = sc.nextLine();
        System.out.print("Enter your Email: ");
        String email = sc.nextLine();
        System.out.print("Enter your Mobile Number: ");
        String phone = sc.nextLine();

        Student s = new Student(id, name, age, gender, department, email, phone);
        students.add(s);
        System.out.println("Student added successfully...");
    }
    public StudentManagement() {
        loadTestData();
    }

    private void loadTestData() {
        students.add(new Student(101, "Alice", 20, "Female", "CSE",
                "alice@gmail.com", "9876543210"));

        students.add(new Student(102, "Bob", 21, "Male", "ECE",
                "bob@gmail.com", "9876543211"));

        students.add(new Student(103, "Charlie", 22, "Male", "IT",
                "charlie@gmail.com", "9876543212"));

        students.add(new Student(104, "Diana", 20, "Female", "EEE",
                "diana@gmail.com", "9876543213"));

        students.add(new Student(105, "Ethan", 23, "Male", "Mechanical",
                "ethan@gmail.com", "9876543214"));

        students.add(new Student(106, "Fiona", 21, "Female", "Civil",
                "fiona@gmail.com", "9876543215"));
    }

    public void viewStudent() {

        System.out.println("Students in list: " + students.size());

        for (Student students : students) {
            System.out.println(students.toString());
        }
        System.out.println("Student listed successfully...");
    }

}
