# Contributing to Java Forge 🛠️

First off, thank you for considering contributing to **Java Forge**! It's people like you that make Java Forge such a great community resource for developers worldwide.

Whether you are fixing a typo, adding automated unit tests, implementing stretch goals, or writing documentation, every single contribution is appreciated and recognized.

---

## 🌟 Why Contribute?

- **Build your Open Source Portfolio:** Your GitHub profile avatar will be permanently displayed on the Java Forge README.
- **Sharpen your Java Skills:** Practice reading, testing, and refining real Java and Spring Boot code.
- **Great for Beginners:** We actively maintain `good first issue` tasks for newcomers to Git and open source.

---

## 💡 Ways You Can Contribute Right Now

1. **🧪 Add Automated Unit Tests (Highest Priority!):**
   - Many early console and intermediate projects need JUnit 5 and Mockito test suites.
   - Example: Adding test cases for `06-Quiz-Application` or `11-Log-Parser`.
2. **📖 Add Swagger / OpenAPI Documentation:**
   - Add `springdoc-openapi-starter-webmvc-ui` to Spring Boot projects (Levels 4–7) to provide an interactive `/swagger-ui/index.html`.
3. **🎯 Add "Stretch Challenge Quests":**
   - Add 2–3 creative stretch goals to any project's `README.md` to challenge ambitious learners.
4. **🐳 Add Dockerfiles / Container Support:**
   - Help containerize projects that don't have a `Dockerfile` yet.
5. **🐛 Bug Fixes & Refactoring:**
   - Optimize queries, fix logical edge cases, or modernize legacy syntax to modern Java 17/21 (e.g. using `record`, text blocks, pattern matching).
6. **📝 Documentation & Typos:**
   - Clarify explanations, fix grammar, or improve installation instructions.

---

## 🚀 Step-by-Step Contribution Workflow

### 1. Fork the Repository
Click the **Fork** button at the top right of this page to create your own copy of the repository.

### 2. Clone Your Fork Locally
```bash
git clone https://github.com/YOUR-USERNAME/Java-Forge.git
cd Java-Forge
```

### 3. Create a New Branch
Create a descriptive branch for your work:
```bash
git checkout -b feat/add-tests-quiz-app
```

### 4. Make Your Changes & Test
Navigate to the specific project you are working on and verify it compiles and tests pass:
```bash
cd level-01-core-java/06-Quiz-Application
mvn clean test
```

### 5. Commit Your Changes
We follow [Conventional Commits](https://www.conventionalcommits.org/):
- `feat:` A new feature or project addition
- `test:` Adding or updating unit/integration tests
- `fix:` Bug fix
- `docs:` Documentation updates, typos, or README improvements
- `refactor:` Code refactoring without changing functionality

```bash
git add .
git commit -m "test: add JUnit 5 test suite for Quiz Application"
```

### 6. Push to Your Fork and Open a Pull Request
```bash
git push origin feat/add-tests-quiz-app
```
Then visit GitHub and click **Compare & pull request**. Fill out the PR template checklist!

---

## 📏 Coding Standards

- **Java Version:** Target Java 17 or Java 21 features where applicable.
- **Naming Conventions:**
  - Classes & Interfaces: `PascalCase` (e.g., `StudentService`)
  - Methods & Variables: `camelCase` (e.g., `findStudentById`)
  - Constants: `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_COUNT`)
  - Packages: lowercase, reverse domain (e.g., `com.javaforge.student`)
- **No Committed Secrets:** Never commit passwords, `.env` files, or local database credentials. Use `.example` files instead.
- **Keep it Clean:** Avoid bloated dependencies; use standard Spring Boot starters or standard library APIs whenever possible.

---

## 🤝 Code of Conduct

Be welcoming, polite, and constructive. We celebrate learners of all skill levels, from absolute beginners writing their first Java line to seasoned architects.

