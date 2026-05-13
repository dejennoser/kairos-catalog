## Kairos Catalog API

> Find the right product at the right time.

Kairos is a production‑style product catalog system built with **Spring Boot** and a **React SPA**, focusing on clean architecture, security, and real‑world integration.

---

## Architecture

The project demonstrates **two architectural approaches**:

- **v1 API** — classic layered architecture
- **v2 API** — Vertical Slice Architecture (use‑case oriented)

v2 endpoints are implemented incrementally without breaking v1.  
The frontend reads products via v1 and creates products via a v2 vertical slice.

---

## Tech Stack

**Backend**
- Java 25, Spring Boot 4
- PostgreSQL, Flyway
- Spring Data JPA / Hibernate
- OpenSearch (full‑text search)
- MinIO (S3‑compatible image storage)
- Keycloak (OAuth2 / JWT)
- Swagger / SpringDoc
- Docker & Docker Compose

**Frontend**
- React + Vite
- Axios
- keycloak‑js
- FormData (JSON + image uploads)

Frontend repo:  
https://github.com/dejennoser/kairos-catalog-frontend

---

## Features

- Product CRUD API
- Full‑text search with OpenSearch
- Product image upload and storage
- Multilingual product content (EN, DE, FR, IT)
- Role‑based access (`ROLE_USER`, `ROLE_ADMIN`)
- JWT authentication via Keycloak
- Swagger API documentation
- React SPA with authenticated product creation

---

## API Versions

- `/api/v1/**` — layered architecture (stable)
- `/api/v2/**` — Vertical Slice Architecture (use‑case based)

---

## Getting Started

### Prerequisites
- Docker
- Java 21+
- Maven (or `./mvnw`)

### Run infrastructure
```bash
docker compose up -d postgres opensearch keycloak minio

## Author

Dejen Teklit — [github.com/dejennoser](https://github.com/dejennoser)