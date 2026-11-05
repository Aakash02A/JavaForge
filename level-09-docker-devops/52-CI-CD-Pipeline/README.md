# 52 - CI/CD Pipeline (GitHub Actions)

This project demonstrates a minimal continuous integration pipeline using GitHub Actions. The workflow checks out the repository, sets up JDK 21, caches Maven dependencies, and runs `mvn clean verify` to build and test the project.

## What is included

- A tiny Java library (`Calculator`) with unit tests.
- A GitHub Actions workflow at `.github/workflows/ci.yml` that runs on push and pull request to `main`/`master`.
- `.gitignore` to exclude build artifacts and local files.

## How the workflow works

1. Trigger on push or pull_request targeting `main`/`master`.
2. Checkout the repo.
3. Set up Temurin JDK 21 and enable Maven caching.
4. Run `mvn -B clean verify` which runs tests and packages the project.

## Run locally

To run the tests locally run:

```bash
mvn test
```

Local test run should pass and return successful results.

## Notes

This project is intentionally minimal so the CI workflow focuses on the core steps (checkout, setup JDK, cache dependencies, build & test). Extend the workflow with linting, coverage, artifact upload, or deployment steps as a next exercise.
