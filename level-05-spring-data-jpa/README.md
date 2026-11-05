# Level 5: Spring Data JPA & Hibernate

Welcome to **Level 5** of Java Forge! This level teaches object-relational mapping (ORM) to eliminate boilerplate database access code.

## 🎯 Concepts Covered
- JPA Entities (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`)
- `JpaRepository` & Spring Data Query Methods
- Relational Mappings (`@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`)
- Cascade Types & Fetch Strategies (`LAZY` vs `EAGER`)
- JPQL and Native SQL Queries (`@Query`)
- Managing the Hibernate Persistence Context & Transactional boundaries (`@Transactional`)

## 🗂️ Projects in this Level

| Project | Key Focus | Description |
| :--- | :--- | :--- |
| [29-Employee-Management](29-Employee-Management/) | Spring Data JPA | Replacing JDBC boilerplate with declarative repository methods |
| [30-Blog-API](30-Blog-API/) | Entity Relationships | Posts, comments, and author relationships with cascade options |
| [31-Library-API](31-Library-API/) | One-to-Many | Mapping authors to multiple books with bi-directional references |
| [32-Hospital-API](32-Hospital-API/) | Many-to-Many | Modeling doctors and patients with join table mappings |
| [33-School-Management](33-School-Management/) | Complex Hibernate | Course enrollments, grades, and JPQL reporting queries |
