package com.javaforge.ranking.service;

import com.javaforge.ranking.model.Student;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StudentRankingService {

    // 1. Sort by Score Descending (Highest score first)
    public List<Student> sortByScoreDescending(List<Student> students) {
        return students.stream()
                .sorted(Comparator.comparingDouble(Student::getScore).reversed())
                .collect(Collectors.toList());
    }

    // 2. Sort by Name Ascending (Alphabetical)
    public List<Student> sortByNameAscending(List<Student> students) {
        return students.stream()
                .sorted(Comparator.comparing(Student::getName))
                .collect(Collectors.toList());
    }

    // 3. Sort by Score Descending, then by Name Ascending (Tie-breaker)
    public List<Student> sortByScoreThenName(List<Student> students) {
        return students.stream()
                .sorted(Comparator.comparingDouble(Student::getScore).reversed()
                        .thenComparing(Student::getName))
                .collect(Collectors.toList());
    }

    // 4. Custom Comparator: Sort by Age Ascending, but if same age, lowest ID first
    public List<Student> sortByAgeThenId(List<Student> students) {
        Comparator<Student> ageThenIdComparator = new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                int ageComparison = Integer.compare(s1.getAge(), s2.getAge());
                if (ageComparison == 0) {
                    return Integer.compare(s1.getId(), s2.getId());
                }
                return ageComparison;
            }
        };

        return students.stream()
                .sorted(ageThenIdComparator)
                .collect(Collectors.toList());
    }
}
