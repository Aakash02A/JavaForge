# Level 7: Advanced Backend Engineering

Welcome to **Level 7** of Java Forge! This level introduces high-performance, asynchronous, and distributed caching patterns essential for enterprise-grade backend systems.

## 🎯 Concepts Covered
- In-memory distributed caching with Redis
- Cache eviction policies (`@Cacheable`, `@CachePut`, `@CacheEvict`)
- API Rate Limiting (Token Bucket algorithm, Redis-backed rate limiting)
- Asynchronous task execution (`@Async`, thread pools)
- Email notification pipelines (`JavaMailSender`)
- Secure local and cloud file upload/storage handling

## 🗂️ Projects in this Level

| Project | Key Focus | Description |
| :--- | :--- | :--- |
| [38-URL-Shortener](38-URL-Shortener/) | High Scale API | Production-ready URL shortener with analytics and redirect caching |
| [39-File-Storage-API](39-File-Storage-API/) | File Handling | Secure upload, validation, streaming, and download endpoints |
| [40-Email-Service](40-Email-Service/) | Java Mail | HTML template rendering and transactional email dispatch |
| [41-Notification-Service](41-Notification-Service/) | Async Events | Non-blocking multi-channel notification engine |
| [42-Redis-Cache-Demo](42-Redis-Cache-Demo/) | Distributed Cache | Drastically reducing database load with Redis cache layers |
| [43-Rate-Limiter](43-Rate-Limiter/) | Throttling | Protecting endpoints from abuse using Redis token bucket |
