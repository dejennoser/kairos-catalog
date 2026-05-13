# Kairos Catalog API

> Find the right product at the right time.

A production-style product catalog REST API built with modern Java and Spring Boot.

---

## Tech stack

| Technology                  | Purpose |
|-----------------------------|---|
| Java 25 + Spring Boot 4     | Core framework |
| PostgreSQL 16               | Primary database |
| Flyway                      | Schema versioning |
| Spring Data JPA + Hibernate | ORM |
| Lombok + JSpecify           | Boilerplate elimination + null safety |
| OpenSearch                  | Fuzzy full-text search |
| MinIO                       | Image storage (S3-compatible) |
| Keycloak 23                 | OAuth2 / JWT authentication |
| Swagger / SpringDoc         | API documentation |
| Docker + Docker Compose     | Containerized infrastructure |
| Maven                       | Build tool |

---

## Features

- Full CRUD product catalog API
- Fuzzy search via OpenSearch — typo-tolerant
- Product image upload and storage via MinIO
- Multilingual error messages — EN, DE, FR, IT
- Multilingual product content — name and description per locale
- Role-based access control — `ROLE_USER` and `ROLE_ADMIN`
- JWT authentication via Keycloak
- Schema migrations via Flyway
- Fully documented via Swagger UI

---
---

## Frontend (React SPA)

The Kairos Catalog is complemented by a modern React Single Page Application.

### Frontend repository
https://github.com/dejennoser/kairos-catalog-frontend

### Frontend tech stack

| Technology | Purpose |
|---|---|
| React + Vite | SPA framework and dev server |
| keycloak-js | OAuth2 / PKCE authentication |
| Axios | HTTP client |
| FormData API | Multipart uploads (JSON + images) |

### Frontend features

- Keycloak authentication using **public client + PKCE**
- Role‑based UI (USER / ADMIN)
- Product listing with localized content
- ADMIN‑only product creation
- Multipart product creation (JSON + images)
- Image display directly from MinIO
- Automatic product list refresh after creation

### Architecture note

Swagger is used for **API documentation only**.

The frontend is the **real client** for multipart requests:
- JSON + image uploads are sent using browser `FormData`
- JWT tokens are refreshed automatically before protected requests
- This mirrors real production SPA behavior
  ``

## Getting started

### Prerequisites

- Docker Desktop running
- Java 21+ installed
- Maven installed or use `./mvnw`

### 1. Clone the repo

```bash
git clone https://github.com/dejennoser/kairos-catalog.git
cd kairos-catalog
```

### 2. Create your `.env` file

```bash
cp .env.example .env
```

Edit `.env` with your values:

```env
POSTGRES_DB=kairos
POSTGRES_USER=kairos
POSTGRES_PASSWORD=kairos
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin
MINIO_ROOT_USER=kairos
MINIO_ROOT_PASSWORD=kairos123
```

### 3. Start infrastructure

```bash
docker compose up -d postgres opensearch keycloak minio
```

### 4. Run the app

```bash
./mvnw spring-boot:run
```

### 5. Open Swagger
http://localhost:8080/swagger-ui/index.html

---

## Project structure
src/main/java/com/kairos/catalog/
├── config/          # Security, MinIO, OpenSearch, Jackson, Swagger
├── controller/      # REST endpoints
├── dto/             # Request and response DTOs
├── entity/          # JPA entities
├── exception/       # Custom exceptions and global handler
├── repository/      # JPA repositories
├── security/        # Roles constants and KeycloakJwtConverter
└── service/         # Business logic

---

## Security & Authorization

This service is secured using Keycloak with OAuth2 / JWT authentication.

### Authentication

- Keycloak issues JWT access tokens
- Spring Boot acts as an OAuth2 Resource Server
- Tokens are validated by:
    - Signature
    - Issuer (`http://localhost:8180/realms/kairos`)
    - Audience / authorized client

### Architecture

The `security` package contains:

- `Roles.java` — constants for role names (`ROLE_ADMIN`, `ROLE_USER`)
- `KeycloakJwtConverter.java` — extracts realm roles from the Keycloak JWT and maps them to Spring Security authorities

### Roles

| Role | Permissions |
|---|---|
| `ROLE_USER` | Read-only access |
| `ROLE_ADMIN` | Full access |

### Endpoint access rules

| Endpoint | USER | ADMIN |
|---|---|---|
| GET /api/v1/products | ✅ | ✅ |
| GET /api/v1/products/{id} | ✅ | ✅ |
| GET /api/v1/products/search | ✅ | ✅ |
| GET /api/v1/products/fuzzy-search | ✅ | ✅ |
| POST /api/v1/products | ❌ | ✅ |
| PUT /api/v1/products/{id} | ❌ | ✅ |
| DELETE /api/v1/products/{id} | ❌ | ✅ |
| POST /api/v1/products/{id}/image | ❌ | ✅ |

### Swagger OAuth2

Swagger UI is integrated with Keycloak using Bearer token authentication.

Steps:
1. Open Swagger UI at `http://localhost:8080/swagger-ui/index.html`
2. Get a token from Keycloak using the PowerShell command below
3. Click **Authorize** 🔒 in Swagger
4. Paste the `access_token` value
5. Click **Authorize** → **Close**
6. Swagger automatically sends the JWT with every request

### Get a token

```powershell
Invoke-WebRequest -Method POST `
  -Uri "http://localhost:8180/realms/kairos/protocol/openid-connect/token" `
  -ContentType "application/x-www-form-urlencoded" `
  -Body "grant_type=password&client_id=kairos-catalog&client_secret=kairos-secret&username=kairosuser&password=Pass1234!" `
  | Select-Object -ExpandProperty Content
```

### Default users

| Username | Password | Role |
|---|---|---|
| `kairosuser` | `Pass1234!` | `ROLE_ADMIN` |
| `kairosviewer` | `View1234!` | `ROLE_USER` |
---

## API endpoints

| Method | Endpoint | Description | Auth required |
|---|---|---|---|
| GET | `/api/v1/products` | Get all products | ROLE_USER |
| GET | `/api/v1/products/{id}` | Get product by ID | ROLE_USER |
| GET | `/api/v1/products/category/{category}` | Filter by category | ROLE_USER |
| GET | `/api/v1/products/search?name=` | Search by name | ROLE_USER |
| GET | `/api/v1/products/fuzzy-search?query=` | Fuzzy search | ROLE_USER |
| POST | `/api/v1/products` | Create product | ROLE_ADMIN |
| PUT | `/api/v1/products/{id}` | Update product | ROLE_ADMIN |
| DELETE | `/api/v1/products/{id}` | Delete product | ROLE_ADMIN |
| POST | `/api/v1/products/with-images` | Create product with images (multipart) | ROLE_ADMIN |

---

### SPA authentication model

- Backend uses a **confidential Keycloak client**
- Frontend uses a **public Keycloak client with PKCE**
- JWT access tokens are refreshed in the browser before protected requests
- This separation follows OAuth2 best practice


## Multilingual support

Pass `Accept-Language` header to get localized responses:
Accept-Language: de  → German
Accept-Language: fr  → French
Accept-Language: it  → Italian
Accept-Language: en  → English (default)

### Multilingual error messages

```json
{
  "status": 404,
  "message": "Produkt mit ID 123 nicht gefunden",
  "timestamp": "2026-05-11T10:00:00"
}
```

### Multilingual product content

Products support translated `name` and `description` per locale via the `translations` field:

```json
{
  "name": "Mechanical Keyboard",
  "translations": {
    "de": {
      "name": "Mechanische Tastatur",
      "description": "RGB hintergrundbeleuchtete Tastatur"
    },
    "fr": {
      "name": "Clavier mécanique",
      "description": "Clavier rétroéclairé RGB"
    },
    "it": {
      "name": "Tastiera meccanica",
      "description": "Tastiera retroilluminata RGB"
    }
  }
}
```

---

## Infrastructure

| Service | URL |
|---|---|
| Spring Boot API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| Keycloak Admin | http://localhost:8180 |
| OpenSearch | http://localhost:9200 |
| MinIO Console | http://localhost:9001 |

---

## Challenges and solutions

| Challenge | Solution |
|---|---|
| `spring-data-opensearch` incompatible with Spring Boot 4 | Used `opensearch-rest-high-level-client` directly |
| Keycloak 24 user always "not fully set up" | Downgraded to Keycloak 23 |
| JWT roles not mapped correctly | Custom `KeycloakJwtConverter` extracting `realm_access.roles` |
| MinIO image URL returning wrong path | Fixed by constructing URL from `minio.url + bucket + filename` |
| Product images are stored in MinIO and served via public object URLs.
---

## Author

Dejen Teklit — [github.com/dejennoser](https://github.com/dejennoser)