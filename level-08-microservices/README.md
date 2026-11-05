# Level 8: Microservices Architecture

Welcome to **Level 8** of Java Forge! This level teaches distributed system architectures using Spring Cloud, decomposing monoliths into independent, resilient services.

## 🎯 Concepts Covered
- Microservice decomposition & domain boundaries
- API Gateway routing and filtering (Spring Cloud Gateway)
- Service Discovery and dynamic registration (Netflix Eureka)
- Centralized configuration management (Spring Cloud Config Server)
- Inter-service communication (REST, OpenFeign)
- Event-driven patterns & asynchronous communication

## 🗂️ Projects in this Level

| Project | Key Focus | Description |
| :--- | :--- | :--- |
| [44-Product-Service](44-Product-Service/) | Domain Service | Independent product catalog microservice |
| [45-Order-Service](45-Order-Service/) | Inter-service Calls | Order processing interacting with inventory/product services |
| [46-Payment-Service](46-Payment-Service/) | Event Integration | Decoupled payment processing workflow |
| [47-API-Gateway](47-API-Gateway/) | Edge Routing | Single entry point with route matching, rewrite rules, and security |
| [48-Service-Discovery](48-Service-Discovery/) | Eureka Registry | Dynamic service registry enabling zero-hardcoded service IPs |
| [49-Config-Server](49-Config-Server/) | Centralized Config | Git-backed environment configuration across all services |

