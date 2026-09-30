package com.javaforge.ranking.model;

public class Student {
    private int id;
    private String name;
    private double score;
    private int age;

    public Student(int id, String name, double score, int age) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.age = age;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getScore() { return score; }
    public int getAge() { return age; }

    @Override
    public String toString() {
        return String.format("Student{id=%d, name='%s', score=%.1f, age=%d}", id, name, score, age);
    }
}
