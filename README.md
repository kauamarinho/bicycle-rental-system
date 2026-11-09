# 🚲 Bicycle Rental System

![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-6DB33F?logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?logo=springsecurity)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?logo=postgresql)
![JUnit5](https://img.shields.io/badge/Tests-JUnit5-25A162?logo=junit5)

A Spring Boot REST API for managing bicycle rentals, including customer registration, reservations, rentals, returns, and payments.
Data is persisted in PostgreSQL with **Spring Data JPA**, and the API is protected by **Spring Security** with stateless **JWT** authentication.

## ✨ Features

- Customer registration with CPF and email validation (CPF is unique)
- Address lookup by CEP (ViaCEP), stored on the customer and shown on rentals
- Listing of customers and bicycles
- Reservation and cancellation of reservations
- Bicycle rental and return with hourly rate calculation
- Rental payment with receipt generation (one payment per rental)
- Automatic bicycle status control
- JWT login, BCrypt-hashed passwords and role-based access (`ADMIN`, `EMPLOYEE`)

## 📁 Project structure

```
src/main/java/org/example/
├── BicycleRentalApplication.java
├── client/                 # CepClient + ViaCEP implementation
├── config/
│   └── InitialDataLoader.java      # seeds bicycles + admin user on first start
├── controller/             # REST controllers (incl. AuthController)
├── dto/ApiDtos.java
├── handler/GlobalExceptionHandler.java
├── domain/
│   ├── model/              # JPA entities: Bicycle, Customer, Rental, Reservation, Payment (+ Address @Embeddable)
│   ├── vo/                 # Cpf, Email, Cep value objects
│   │   └── converter/      # JPA AttributeConverters for Cpf and Email
│   ├── enums/
│   └── exception/
├── repository/             # Spring Data JPA repositories
├── security/               # SecurityConfig, TokenService, AppUser, AppUserService, Role
└── service/                # @Transactional business services

src/test/java/org/example/
├── client/viacep/ViaCepClientTest.java
├── repository/RepositoryMappingTest.java     # @DataJpaTest (H2)
├── security/SecurityIntegrationTest.java     # JWT rules end to end (MockMvc)
└── service/*ServiceTest.java                 # unit tests with Mockito
```

## 🛠️ Technologies

- Java 25, Maven
- Spring Boot 4 (Web MVC)
- Spring Data JPA / Hibernate
- PostgreSQL 17 (Docker Compose) — H2 in tests
- Spring Security + OAuth2 Resource Server (JWT HS256), BCrypt
- JUnit 5, Mockito, MockMvc

## 🚀 How to run

### Prerequisites

- JDK 25
- Docker running (the app starts `compose.yaml` automatically via `spring-boot-docker-compose`)

### Run the tests

```bash
./mvnw test
```

Tests use an in-memory H2 database and don't need Docker.

### Run the application

```bash
./mvnw spring-boot:run
```

On the first start the tables are created, five bicycles are seeded and an admin user is created.

| Variable | Default (dev only) |
|---|---|
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | `jdbc:postgresql://localhost:5432/bicycle_rental` / `bike` / `bike` |
| `JWT_SECRET` (≥ 32 bytes) | dev secret in `application.yaml` |
| `ADMIN_USERNAME` / `ADMIN_PASSWORD` | `admin` / `admin123` |

> Override `JWT_SECRET` and `ADMIN_PASSWORD` in any non-local environment.

## 🔐 Authentication

```bash
# 1. log in and get a token
curl -X POST localhost:8080/v1/auth/login -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"admin123"}'
# → {"token":"eyJ...","tokenType":"Bearer","expiresAt":"..."}

# 2. call protected endpoints with the token
curl localhost:8080/v1/customers -H "Authorization: Bearer eyJ..."
```

Admins can create staff users:

```json
POST /v1/auth/users
{ "username": "joao", "password": "secret123", "role": "EMPLOYEE" }
```

| Endpoint | Access |
|---|---|
| `POST /v1/auth/login` | public |
| `GET /v1/bicycles/**`, `GET /v1/addresses/**` | public |
| `POST /v1/auth/users` | `ADMIN` |
| `/v1/customers/**`, `/v1/rentals/**`, `/v1/reservations/**` | `ADMIN`, `EMPLOYEE` |

Missing/invalid token → `401`; authenticated without the required role → `403`.

## 📖 Endpoints

All under `/v1` (port 8080): `GET/POST /customers`, `GET /customers/{id}`, `GET /bicycles`, `GET /bicycles/{id}`,
`GET/POST /rentals`, `GET /rentals/{id}`, `POST /rentals/{id}/return`, `POST /rentals/{id}/payment`,
`GET/POST /reservations`, `POST /reservations/{id}/cancel`, `GET /addresses/{cep}`.

Registering a customer requires a `cep`; the address is fetched from ViaCEP:

```json
POST /v1/customers
{ "name": "Ana", "cpf": "123.456.789-01", "email": "ana@email.com", "cep": "01310-100" }
```

Typical flow: register a customer → reserve or rent a bicycle → return it → pay.

## 📋 Business rules

- CPF must contain 11 numeric digits, ignoring dots and dashes, and is unique (`409` if already registered)
- Email must be valid
- CEP must contain 8 numeric digits (dash is ignored) and must exist in ViaCEP
- Only available bicycles can be reserved
- Only bicycles that are not rented, reserved or removed can be rented out
- Returns require hours greater than zero
- Payment is only allowed for finalized rentals, once per rental

Errors: invalid input → `400`, not found → `404`, business conflict → `409`, ViaCEP unreachable → `502`.

## 🏗️ Architecture notes

- `Cpf` and `Email` are Value Objects persisted through JPA `AttributeConverter`s; `Address` is an `@Embeddable` record
- Services are `@Transactional`; entity changes (e.g. bicycle status) are saved by dirty checking
- `open-in-view` is disabled; rental/reservation queries use `@EntityGraph` to load customer and bicycle in one query
- JWTs are issued and validated with Spring Security's Nimbus encoder/decoder — no extra JWT library

## 👤 Author

Kauã Marinho
