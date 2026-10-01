# Project Idea

Build a **Log Parser** that reads a server log file, parses each log entry, filters by severity level, and writes the results to an output file.

# Problem Statement

Production servers generate thousands of log lines daily. Manually scanning log files to find errors or warnings is tedious and time-consuming.

The **Log Parser** automates this by reading a `.log` file, parsing each line into structured `LogEntry` objects, generating summary statistics, and exporting filtered results (e.g., only ERROR logs) to a separate report file.

# Objectives

- Read log entries from a text file using Java NIO.
- Parse each line into a structured `LogEntry` object.
- Skip malformed or invalid lines gracefully.
- Count entries by log level (INFO, WARN, ERROR, DEBUG).
- Filter entries by a specific log level.
- Write filtered log entries to an output file.

# Features

- File Reading (Java NIO)
- Log Line Parser
- Invalid Line Skipper
- Log Level Counter
- Log Level Filter
- File Writer (output report)

# Log Entry Details

- Timestamp
- Level (INFO / WARN / ERROR / DEBUG)
- Message

# Tech Stack

- Java (Intermediate)
- Java NIO (java.nio.file.Files, Paths)
- Java Streams API
- File I/O (Reading & Writing)
- String Parsing

# Suggested Class Structure

LogEntry
---------
timestamp
level
message

LogParserService
----------------
parseLogFile()
countLogLevels()
filterByLevel()
writeLogsToFile()

Main
----
main()
createDummyLogFile()

# Sample Log Format

[2026-09-30 10:15:33] ERROR - Database connection failed: Timeout
[2026-09-30 10:16:00] INFO - Database connection restored
This is an invalid log line that should be skipped

# Sample Output

Parsing log file: server.log
Total valid log entries parsed: 6

Log Level Counts:
ERROR: 2
INFO: 2
DEBUG: 1
WARN: 1

Filtering ERROR logs...
Found 2 ERROR entries.
Writing ERROR logs to: error_logs.txt
Write complete.

# Learning Outcomes

By completing this project, you will learn:

- Reading files line-by-line using Java NIO (`Files.lines()`)
- Writing files using `Files.write()`
- Parsing raw strings into structured objects
- Handling malformed input gracefully
- Using Streams with file data
- Separating concerns into service layers

# Project Details

Difficulty: ⭐⭐☆☆☆ (Intermediate)

Estimated Time: 5–8 hours

Skills Gained:
- Java NIO File Processing
- String Parsing
- Java Streams
- File I/O
