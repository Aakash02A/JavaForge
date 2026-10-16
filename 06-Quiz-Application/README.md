# 06 - Quiz Application

A Java console multiple-choice quiz. Questions and four options are loaded from `questions.csv`; the application checks the selected option and prints the final score.

## Features

- Load questions and options from a CSV file.
- Show four numbered options for each question.
- Accept a numbered answer and report whether it is correct.
- Display the final score after all questions.

## Question file format

The CSV file must have a header followed by rows in this order:

```csv
Question,Option 1,Option 2,Option 3,Option 4,CorrectOptionIndex
What is the capital of France?,London,Paris,Rome,Berlin,2
```

`CorrectOptionIndex` is one-based (`1` through `4`). The current loader splits each row on commas, so question and option text must not contain commas. Keep `questions.csv` in the working directory when launching the application; the bundled file is in this project directory.

## Design and limitations

- `Question` holds a question, its options, and the correct option index.
- `QuizService` reads the CSV and provides the loaded question list.
- `Main` presents the quiz through standard input and prints the score.

The quiz is read-only: questions are not added through an admin menu, and results are not persisted. Invalid answer input is marked incorrect. The CSV loader is intentionally simple and is not a complete quoted-CSV parser.

## Requirements and run

- JDK 21
- Maven

From this directory:

```bash
mvn clean package
java -cp target/classes com.javaforge.quiz.Main
```

Run `com.javaforge.quiz.Main` from an IDE with the project root as the working directory if the CSV is not found.

## Tests

Run `mvn test` to compile the project and execute any configured tests. This project currently has no automated test classes, so verify loading and scoring by running the quiz manually.
