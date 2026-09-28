package com.javaforge.student.service;

import com.javaforge.student.model.Student;

import java.util.ArrayList;
import java.util.Scanner;

public class StudentManagement {
    private ArrayList<Student> students = new ArrayList<>();
    private Scanner sc;

    public StudentManagement() {
        this(new Scanner(System.in));
    }

    public StudentManagement(Scanner sc) {
        this.sc = sc;
        loadTestData();
    }

    private void loadTestData() {
        students.add(new Student(1, "Alice", 20, "Female", "CSE",
                "alice@gmail.com", "9876543210"));

        students.add(new Student(2, "Bob", 21, "Male", "ECE",
                "bob@gmail.com", "9876543211"));

        students.add(new Student(3, "Charlie", 22, "Male", "IT",
                "charlie@gmail.com", "9876543212"));

        students.add(new Student(4, "Diana", 20, "Female", "EEE",
                "diana@gmail.com", "9876543213"));

        students.add(new Student(5, "Ethan", 23, "Male", "Mechanical",
                "ethan@gmail.com", "9876543214"));

        students.add(new Student(6, "Fiona", 21, "Female", "Civil",
                "fiona@gmail.com", "9876543215"));
    }

    public Student findStudentById(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return student;
            }
        }
        return null;
    }

    public void addStudent() {
        System.out.print("Enter your ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        if (findStudentById(id) != null) {
            System.out.println("Error: Student with ID " + id + " already exists!");
            return;
        }

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

    public void viewStudent() {
        if (students.isEmpty()) {
            System.out.println("No students found in the list.");
            return;
        }

        System.out.println("Students in list: " + students.size());
        for (Student student : students) {
            System.out.println(student.toString());
        }
        System.out.println("Student listed successfully...");
    }

    public void searchStudent() {
        System.out.print("Enter student ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        Student student = findStudentById(id);
        if (student != null) {
            System.out.println("Student found:");
            System.out.println(student);
        } else {
            System.out.println("Student with ID " + id + " not found.");
        }
    }

    public void updateStudent() {
        System.out.print("Enter student ID to update: ");
        int id = sc.nextInt();
        sc.nextLine();

        Student student = findStudentById(id);
        if (student == null) {
            System.out.println("Student with ID " + id + " not found.");
            return;
        }

        System.out.println("Student found: " + student);
        System.out.println("Select field to update:");
        System.out.println("1. Update All Details");
        System.out.println("2. Update Name");
        System.out.println("3. Update Age");
        System.out.println("4. Update Gender");
        System.out.println("5. Update Department");
        System.out.println("6. Update Email");
        System.out.println("7. Update Mobile Number");
        System.out.print("Enter choice: ");
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1:
                System.out.print("Enter new Name: ");
                student.setName(sc.nextLine());
                System.out.print("Enter new Age: ");
                student.setAge(sc.nextInt());
                sc.nextLine();
                System.out.print("Enter new Gender: ");
                student.setGender(sc.nextLine());
                System.out.print("Enter new Department: ");
                student.setDepartment(sc.nextLine());
                System.out.print("Enter new Email: ");
                student.setEmail(sc.nextLine());
                System.out.print("Enter new Mobile Number: ");
                student.setPhone(sc.nextLine());
                System.out.println("Student updated successfully...");
                break;
            case 2:
                System.out.print("Enter new Name: ");
                student.setName(sc.nextLine());
                System.out.println("Name updated successfully...");
                break;
            case 3:
                System.out.print("Enter new Age: ");
                student.setAge(sc.nextInt());
                sc.nextLine();
                System.out.println("Age updated successfully...");
                break;
            case 4:
                System.out.print("Enter new Gender: ");
                student.setGender(sc.nextLine());
                System.out.println("Gender updated successfully...");
                break;
            case 5:
                System.out.print("Enter new Department: ");
                student.setDepartment(sc.nextLine());
                System.out.println("Department updated successfully...");
                break;
            case 6:
                System.out.print("Enter new Email: ");
                student.setEmail(sc.nextLine());
                System.out.println("Email updated successfully...");
                break;
            case 7:
                System.out.print("Enter new Mobile Number: ");
                student.setPhone(sc.nextLine());
                System.out.println("Mobile number updated successfully...");
                break;
            default:
                System.out.println("Invalid choice! Update cancelled.");
        }
    }

    public void deleteStudent() {
        System.out.print("Enter student ID to delete: ");
        int id = sc.nextInt();
        sc.nextLine();

        Student student = findStudentById(id);
        if (student != null) {
            students.remove(student);
            System.out.println("Student with ID " + id + " deleted successfully...");
        } else {
            System.out.println("Student with ID " + id + " not found.");
        }
    }
}
