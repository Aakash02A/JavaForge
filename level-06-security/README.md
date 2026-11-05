# Level 6: Spring Security & Authentication

Welcome to **Level 6** of Java Forge! This level focuses on securing web APIs, authenticating clients, and enforcing granular access permissions.

## 🎯 Concepts Covered
- Spring Security Architecture (`SecurityFilterChain`, filters, authentication providers)
- Stateless authentication with JSON Web Tokens (JWT)
- Role-Based Access Control (RBAC) & Method Security (`@PreAuthorize`)
- OAuth 2.0 and Social Logins (Google / GitHub)
- Refresh Token rotation and secure token storage strategies
- Password hashing with `BCryptPasswordEncoder`

## 🗂️ Projects in this Level

| Project | Key Focus | Description |
| :--- | :--- | :--- |
| [34-JWT-Authentication](34-JWT-Authentication/) | Stateless JWT | Issue, sign, validate, and extract claims from JWT tokens |
| [35-Role-Based-Access](35-Role-Based-Access/) | RBAC | Enforcing endpoint rules across `USER` and `ADMIN` roles |
| [36-OAuth-Login](36-OAuth-Login/) | OAuth 2.0 / OIDC | Integrating third-party social login workflows |
| [37-Refresh-Token](37-Refresh-Token/) | Token Lifecycle | Short-lived access tokens paired with revocable refresh tokens |

