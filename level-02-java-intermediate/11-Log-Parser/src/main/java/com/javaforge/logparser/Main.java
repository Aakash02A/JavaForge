package com.javaforge.logparser;

import com.javaforge.logparser.model.LogEntry;
import com.javaforge.logparser.service.LogParserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Java Forge Log Parser ===");

        String inputFilePath = "server.log";
        String outputFilePath = "error_logs.txt";

        // 1. Create a dummy log file for testing
        createDummyLogFile(inputFilePath);

        LogParserService parserService = new LogParserService();

        try {
            // 2. Parse the log file
            System.out.println("Parsing log file: " + inputFilePath);
            List<LogEntry> entries = parserService.parseLogFile(inputFilePath);
            System.out.println("Total valid log entries parsed: " + entries.size());

            // 3. Count log levels
            System.out.println("\nLog Level Counts:");
            Map<String, Long> counts = parserService.countLogLevels(entries);
            counts.forEach((level, count) -> System.out.println(level + ": " + count));

            // 4. Filter and Write ERROR logs
            System.out.println("\nFiltering ERROR logs...");
            List<LogEntry> errorLogs = parserService.filterByLevel(entries, "ERROR");
            System.out.println("Found " + errorLogs.size() + " ERROR entries.");

            System.out.println("Writing ERROR logs to: " + outputFilePath);
            parserService.writeLogsToFile(errorLogs, outputFilePath);
            System.out.println("Write complete.");
            
            // Validate write success by printing the new file contents
            System.out.println("\nContents of " + outputFilePath + ":");
            Files.readAllLines(Paths.get(outputFilePath)).forEach(System.out::println);
            
            System.out.println("\n=== Testing Edge Cases ===");

            // Edge Case 1: Missing File
            System.out.println("1. Testing Missing File:");
            try {
                parserService.parseLogFile("does_not_exist.log");
            } catch (IOException e) {
                System.out.println("Caught expected exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }

            // Edge Case 2: Empty File
            System.out.println("\n2. Testing Empty File:");
            String emptyFilePath = "empty.log";
            Files.write(Paths.get(emptyFilePath), Arrays.asList());
            List<LogEntry> emptyEntries = parserService.parseLogFile(emptyFilePath);
            System.out.println("Parsed entries from empty file: " + emptyEntries.size());

            // Edge Case 3: Completely Invalid Data File
            System.out.println("\n3. Testing Completely Invalid File:");
            String invalidFilePath = "invalid.log";
            Files.write(Paths.get(invalidFilePath), Arrays.asList(
                    "just some random text",
                    "[not a timestamp] missing dash",
                    "   ",
                    "",
                    "[2026-09-30] NOLEVEL-Message"
            ));
            List<LogEntry> invalidEntries = parserService.parseLogFile(invalidFilePath);
            System.out.println("Parsed valid entries from heavily corrupted file: " + invalidEntries.size());

        } catch (IOException e) {
            System.err.println("An error occurred during file processing: " + e.getMessage());
        }
    }

    private static void createDummyLogFile(String filePath) {
        Path path = Paths.get(filePath);
        List<String> dummyLogs = Arrays.asList(
                "[2026-09-30 10:00:01] INFO - Application started successfully",
                "[2026-09-30 10:05:12] DEBUG - User authenticated: admin",
                "[2026-09-30 10:15:33] ERROR - Database connection failed: Timeout",
                "[2026-09-30 10:15:35] WARN - Retrying database connection...",
                "[2026-09-30 10:16:00] INFO - Database connection restored",
                "This is an invalid log line that should be skipped",
                "[2026-09-30 10:20:00] ERROR - NullPointerException in PaymentService"
        );
        try {
            Files.write(path, dummyLogs);
            System.out.println("Created dummy log file: " + filePath + "\n");
        } catch (IOException e) {
            System.err.println("Failed to create dummy log file.");
        }
    }
}
