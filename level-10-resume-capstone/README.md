# Level 10: Resume Capstone Project

## 🎯 Capstone: Team Project Management Platform (Full-Stack)

The Level 10 capstone is the culmination of the entire **Java Forge** learning path. Instead of following a pre-built reference implementation, your goal is to design, develop, test, containerize, and deploy a production-grade backend and full-stack application from scratch.

---

## 🛠️ Recommended Tech Stack

- **Backend:** Java 17+, Spring Boot 3 (REST API, layered architecture)
- **Database & Persistence:** MySQL or PostgreSQL, Spring Data JPA, Hibernate, Flyway or Liquibase
- **Security:** Spring Security 6, JWT Authentication, Refresh Token Rotation, Role-Based Access Control (RBAC)
- **Caching & Rate Limiting:** Redis
- **Testing:** JUnit 5, Mockito, Testcontainers
- **DevOps & Cloud:** Docker, Docker Compose, GitHub Actions (CI/CD), AWS EC2 or cloud container hosting
- **Frontend (Optional / Recommended):** React, Vue, or Next.js

---

## 📋 Core Architectural & Business Requirements

1. **User & Team Management (RBAC):**
   - User registration and login with BCrypt password hashing.
   - Roles: `ADMIN`, `PROJECT_MANAGER`, `MEMBER`.
   - Teams / Workspaces with member invites and role assignment.

2. **Project & Task Workflow Engine:**
   - Projects containing Tasks / Issues.
   - Status transitions (e.g., `TODO` -> `IN_PROGRESS` -> `IN_REVIEW` -> `DONE`).
   - Task assignment, priority levels (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`), and due dates.
   - Activity log / audit trail for changes.

3. **Performance & Security Best Practices:**
   - Redis caching for frequently accessed, read-heavy endpoints (e.g., project details, user profiles).
   - API rate limiting to protect sensitive endpoints (login, forgot password).
   - Proper input validation (`jakarta.validation`), global exception handling (`@RestControllerAdvice`), and standard error response DTOs.

4. **Testing & Quality Assurance:**
   - Unit tests for services and domain logic.
   - Integration tests using `@SpringBootTest` and Testcontainers for real database tests.

5. **Containerization & CI/CD:**
   - Multi-stage `Dockerfile` creating a lightweight, secure container image.
   - `docker-compose.yml` spinning up the application, MySQL/Postgres database, and Redis.
   - GitHub Actions workflow running automated tests and building Docker images on push.

---

## 🚀 Suggested Milestone Progression

- [ ] **Milestone 1: Domain Modeling & Database Setup** (Entities, JPA repositories, migrations)
- [ ] **Milestone 2: Authentication & Authorization** (Spring Security, JWT filter, RBAC)
- [ ] **Milestone 3: Core Business Logic & REST Endpoints** (Projects, tasks, comments, services)
- [ ] **Milestone 4: Caching & Optimization** (Redis integration, query tuning)
- [ ] **Milestone 5: Comprehensive Automated Testing** (Unit & Integration tests)
- [ ] **Milestone 6: Dockerization & CI/CD Pipeline**
- [ ] **Milestone 7: Deployment & Documentation** (Live demo URL, API documentation via Swagger/OpenAPI)

