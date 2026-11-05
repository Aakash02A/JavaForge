# Level 4: Spring Boot Basics

Welcome to **Level 4** of Java Forge! This level introduces the Spring Boot framework for modern web APIs and backend services.

## 🎯 Concepts Covered
- Spring Boot Starter dependencies and auto-configuration
- RESTful API design (`@RestController`, `@GetMapping`, `@PostMapping`, etc.)
- Dependency Injection and Inversion of Control (IoC)
- Layered Architecture (`Controller` -> `Service` -> `Repository`)
- Request validation with `jakarta.validation` (`@Valid`, `@NotNull`, `@Size`)
- Pagination, Sorting, and query parameter handling
- Consuming 3rd-party REST APIs using `RestClient` or `RestTemplate`

## 🗂️ Projects in this Level

| Project | Key Focus | Description |
| :--- | :--- | :--- |
| [23-Todo-REST-API](23-Todo-REST-API/) | REST Conventions | Clean HTTP status codes and standard CRUD REST endpoints |
| [24-Notes-API](24-Notes-API/) | Layered Architecture | Separation of presentation, service, and data layers |
| [25-Student-API](25-Student-API/) | Input Validation | Validating payloads and centralized exception responses |
| [26-Book-Store-API](26-Book-Store-API/) | Service Design | Rich business logic separation with DTO mappings |
| [27-Movie-Review-API](27-Movie-Review-API/) | Pagination & Sorting | Handling paginated queries with `Pageable` and Sort orders |
| [28-Weather-API](28-Weather-API/) | External APIs | Fetching, transforming, and serving third-party weather data |
