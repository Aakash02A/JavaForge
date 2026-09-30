package com.javaforge.ranking;

import com.javaforge.ranking.model.Student;
import com.javaforge.ranking.service.StudentRankingService;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Java Forge Student Ranking System (Comparators) ===");

        List<Student> students = Arrays.asList(
                new Student(1, "Alice", 85.5, 20),
                new Student(2, "Bob", 92.0, 21),
                new Student(3, "Charlie", 85.5, 19),
                new Student(4, "Diana", 98.5, 22),
                new Student(5, "Eve", 74.0, 20),
                new Student(6, "Frank", 92.0, 19)
        );

        StudentRankingService rankingService = new StudentRankingService();

        System.out.println("\nOriginal List:");
        students.forEach(System.out::println);

        System.out.println("\n1. Sorted by Score (Descending):");
        rankingService.sortByScoreDescending(students).forEach(System.out::println);

        System.out.println("\n2. Sorted by Name (Alphabetical):");
        rankingService.sortByNameAscending(students).forEach(System.out::println);

        System.out.println("\n3. Sorted by Score (Desc), then Name (Asc):");
        // Notice Bob (92.0) and Frank (92.0) -> Bob comes first
        // Notice Alice (85.5) and Charlie (85.5) -> Alice comes first
        rankingService.sortByScoreThenName(students).forEach(System.out::println);

        System.out.println("\n4. Sorted by Age (Asc), then ID (Asc):");
        rankingService.sortByAgeThenId(students).forEach(System.out::println);
        
        System.out.println("\n=== Testing Edge Cases ===");
        
        // Edge Case 1: Empty List
        System.out.println("1. Testing Empty List:");
        List<Student> emptyList = Arrays.asList();
        System.out.println("Sorted by Score (Empty): " + rankingService.sortByScoreDescending(emptyList));

        // Edge Case 2: List with completely identical attributes
        System.out.println("\n2. Testing Identical Students:");
        List<Student> identicalStudents = Arrays.asList(
                new Student(1, "Clone", 100.0, 20),
                new Student(1, "Clone", 100.0, 20),
                new Student(1, "Clone", 100.0, 20)
        );
        rankingService.sortByScoreThenName(identicalStudents).forEach(System.out::println);
        
        // Edge Case 3: List with single student
        System.out.println("\n3. Testing Single Student:");
        List<Student> singleStudent = Arrays.asList(new Student(99, "Lone Wolf", 50.0, 30));
        rankingService.sortByNameAscending(singleStudent).forEach(System.out::println);
    }
}
