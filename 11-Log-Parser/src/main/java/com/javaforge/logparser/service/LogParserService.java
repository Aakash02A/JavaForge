package com.javaforge.logparser.service;

import com.javaforge.logparser.model.LogEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LogParserService {

    public List<LogEntry> parseLogFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.lines(path)
                .map(this::parseLine)
                .filter(entry -> entry != null)
                .collect(Collectors.toList());
    }

    private LogEntry parseLine(String line) {
        // Expected format: [YYYY-MM-DD HH:MM:SS] LEVEL - Message
        if (line == null || line.trim().isEmpty() || !line.startsWith("[")) {
            return null; // Skip invalid lines
        }
        try {
            int closeBracketIndex = line.indexOf(']');
            if (closeBracketIndex == -1) return null;
            
            String timestamp = line.substring(1, closeBracketIndex);
            
            int dashIndex = line.indexOf('-', closeBracketIndex);
            if (dashIndex == -1) return null;
            
            String level = line.substring(closeBracketIndex + 1, dashIndex).trim();
            String message = line.substring(dashIndex + 1).trim();
            
            return new LogEntry(timestamp, level, message);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Long> countLogLevels(List<LogEntry> entries) {
        return entries.stream()
                .collect(Collectors.groupingBy(LogEntry::getLevel, Collectors.counting()));
    }

    public List<LogEntry> filterByLevel(List<LogEntry> entries, String level) {
        return entries.stream()
                .filter(e -> e.getLevel().equalsIgnoreCase(level))
                .collect(Collectors.toList());
    }

    public void writeLogsToFile(List<LogEntry> entries, String outputPath) throws IOException {
        Path path = Paths.get(outputPath);
        List<String> lines = entries.stream()
                .map(LogEntry::toString)
                .collect(Collectors.toList());
        Files.write(path, lines);
    }
}
