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
    public void viewStudent(){

        System.out.println("Students in list: " + students.size());

        for(Student students : students){
            System.out.println(students.toString());
        }
        System.out.println("Student listed successfully...");
    }

}
