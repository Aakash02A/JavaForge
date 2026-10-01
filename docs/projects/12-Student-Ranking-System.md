# Project Idea

Build a **Student Ranking System** that sorts a list of students using multiple `Comparator` strategies.

# Problem Statement

Schools and exam boards need to rank students in different ways — by score, alphabetically, by age, or using complex tie-breaking rules. Hardcoding a single sort order is inflexible.

The **Student Ranking System** demonstrates how Java's `Comparator` interface enables flexible, reusable, and chainable sorting strategies for complex objects.

# Objectives

- Sort students by score (highest to lowest).
- Sort students alphabetically by name.
- Sort by score with name as a tie-breaker.
- Sort by age, then by ID as a tie-breaker (using a legacy anonymous Comparator).

# Features

- Score-Based Ranking (Descending)
- Alphabetical Ranking
- Chained Comparator (Score + Name tie-breaker)
- Custom Anonymous Comparator (Age + ID tie-breaker)
- Edge Case Handling (empty list, identical students, single student)

# Student Details

- Student ID
- Name
- Score
- Age

# Tech Stack

- Java (Intermediate)
- java.util.Comparator
- Comparator.comparing() / comparingDouble()
- .reversed() and .thenComparing()
- Anonymous Inner Classes
- Java Streams

# Suggested Class Structure

Student
-------
id
name
score
age

StudentRankingService
---------------------
sortByScoreDescending()
sortByNameAscending()
sortByScoreThenName()
sortByAgeThenId()

Main
----
main()

# Sample Output

Original List:
Student{id=1, name='Alice', score=85.5, age=20}
Student{id=2, name='Bob', score=92.0, age=21}

1. Sorted by Score (Descending):
Student{id=4, name='Diana', score=98.5, age=22}
Student{id=2, name='Bob', score=92.0, age=21}

3. Sorted by Score (Desc), then Name (Asc):
Student{id=4, name='Diana', score=98.5, age=22}
Student{id=2, name='Bob', score=92.0, age=21}  <- Bob before Frank (tied at 92.0)
Student{id=6, name='Frank', score=92.0, age=19}

# Learning Outcomes

By completing this project, you will learn:

- Using `Comparator.comparing()` and `Comparator.comparingDouble()`
- Reversing a comparator with `.reversed()`
- Chaining comparators with `.thenComparing()` for tie-breaking
- Writing legacy-style anonymous Comparators
- The difference between modern (lambda) and classic (anonymous class) comparators
- Sorting complex objects safely without mutating the original list

# Project Details

Difficulty: ⭐⭐☆☆☆ (Intermediate)

Estimated Time: 4–6 hours

Skills Gained:
- Java Comparator Interface
- Chained Sorting Logic
- Lambda Expressions
- Anonymous Inner Classes
