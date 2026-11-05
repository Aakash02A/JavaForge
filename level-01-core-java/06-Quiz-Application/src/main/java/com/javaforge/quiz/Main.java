package com.javaforge.quiz;

import com.javaforge.quiz.model.Question;
import com.javaforge.quiz.service.QuizService;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to the Quiz Application!");
        QuizService quizService = new QuizService();
        String filePath = "questions.csv";
        if (!new java.io.File(filePath).exists()) {
            filePath = "06-Quiz-Application/questions.csv";
        }
        quizService.loadQuestions(filePath);

        List<Question> questions = quizService.getQuestions();
        if (questions.isEmpty()) {
            System.out.println("No questions available to start the quiz. Please ensure questions.csv is present and valid.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        int score = 0;

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            System.out.println("\nQ" + (i + 1) + ": " + q.getQuestionText());
            List<String> options = q.getOptions();
            for (int j = 0; j < options.size(); j++) {
                System.out.println((j + 1) + ". " + options.get(j));
            }
            
            System.out.print("Your answer (1-" + options.size() + "): ");
            int answer = -1;
            try {
                answer = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                // Invalid input handling
            }

            if (answer == q.getCorrectOptionIndex()) {
                System.out.println("Correct!");
                score++;
            } else {
                System.out.println("Wrong! The correct answer was " + q.getCorrectOptionIndex() + ". " + options.get(q.getCorrectOptionIndex() - 1));
            }
        }

        System.out.println("\n--- Quiz Finished ---");
        System.out.println("Your final score is: " + score + "/" + questions.size());
    }
}
