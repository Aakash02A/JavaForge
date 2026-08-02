package org.example;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        boolean proceed = true;

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
            StudentManagement sm = new StudentManagement();

            switch (choice) {
                case 1:
                    System.out.println("===== Enter Student Data =====");
                    sm.addStudent();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    proceed = false;
                    break;
                default:
                    System.out.println("Invalid");
            }
        }

    }
}