package com.javaforge.student;

import com.javaforge.student.service.StudentManagement;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean proceed = true;

        StudentManagement sm = new StudentManagement(sc);
        while (proceed) {
            System.out.println("\n===== Student Management System =====");
            System.out.println("1. Add Student");
            System.out.println("2. View Students");
            System.out.println("3. Search Student");
            System.out.println("4. Update Student");
            System.out.println("5. Delete Student");
            System.out.println("6. Exit");

            System.out.print("Enter Choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("===== Enter Student Data =====");
                    sm.addStudent();
                    break;
                case 2:
                    System.out.println("===== View Student Data =====");
                    sm.viewStudent();
                    break;
                case 3:
                    System.out.println("===== Search Student =====");
                    sm.searchStudent();
                    break;
                case 4:
                    System.out.println("===== Update Student =====");
                    sm.updateStudent();
                    break;
                case 5:
                    System.out.println("===== Delete Student =====");
                    sm.deleteStudent();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    proceed = false;
                    break;
                default:
                    System.out.println("Invalid Choice! Please enter a number between 1 and 6.");
            }
        }
    }
}
