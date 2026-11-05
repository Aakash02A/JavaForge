package com.javaforge.quiz.service;

import com.javaforge.quiz.model.Question;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuizService {
    private List<Question> questions = new ArrayList<>();

    public void loadQuestions(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) { 
                    isFirstLine = false;
                    continue; // Skip header
                }
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String qText = parts[0];
                    List<String> options = Arrays.asList(parts[1], parts[2], parts[3], parts[4]);
                    int correctIdx = Integer.parseInt(parts[5].trim());
                    questions.add(new Question(qText, options, correctIdx));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading questions file: " + e.getMessage());
        }
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
