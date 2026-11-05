# 16 - URL Shortener

A small Java library that maps long URLs to short URL strings and back. It uses random UUID-derived codes and in-memory maps to demonstrate hashing, reverse lookups, and input validation.

## Features

- Shorten a non-empty URL.
- Return the same short URL when a previously shortened long URL is submitted again.
- Expand a short URL created by this instance.
- Reject null, empty, or incorrectly prefixed short URL inputs.

## Usage

Create a `UrlShortener` instance and call its public methods:

```java
UrlShortener shortener = new UrlShortener();
String shortUrl = shortener.shortenUrl("https://example.com/a/long/path");
String originalUrl = shortener.expandUrl(shortUrl);
```

The generated short URLs use the fixed base `http://short.url/`. The project is a library, not a web service or console application; it does not persist mappings, validate URL syntax, or prevent the theoretical collision of two generated six-character codes. Each new instance starts with an empty mapping.

## Build and tests

Requirements: JDK 21 and Maven.

```bash
mvn clean test
```

JUnit tests cover shortening, expansion, repeat shortening, and invalid input. Use `mvn package` to build the JAR.
