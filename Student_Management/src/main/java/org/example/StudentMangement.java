package org.example;

import java.util.ArrayList;
import java.util.Scanner;

public class StudentMangement {
    private ArrayList<Student> students = new ArrayList<>();
    private Scanner sc = new Scanner(System.in);


    public void addStudent() {
        int id = sc.nextInt();
        String name = sc.nextLine();
        int age = sc.nextInt();
        String gender = sc.nextLine();
        String department = sc.nextLine();
        String email = sc.nextLine();
        String phone = sc.nextLine();

        Student s = new Student(id, name, age, gender, department, email, phone);

    }
}
