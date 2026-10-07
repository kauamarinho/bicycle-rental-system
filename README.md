# 🚲 Bicycle Rental System

![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)
![Maven](https://img.shields.io/badge/Maven-Build-blue?logo=apachemaven)
![JUnit5](https://img.shields.io/badge/Tests-JUnit5-25A162?logo=junit5)

A Spring Boot REST API for managing bicycle rentals, including customer registration, reservations, rentals, returns, and payments.

The project is organized to cleanly separate the console interface, the domain, and the in-memory infrastructure, and already includes automated tests for the services.

## ✨ Features

- Customer registration with CPF and email validation
- Address lookup by CEP (ViaCEP), stored on the customer and shown on rentals
- Listing of customers and bicycles
- Reservation and cancellation of reservations
- Bicycle rental and return with hourly rate calculation
- Rental payment with receipt generation
- Automatic bicycle status control
- REST API (Spring Boot) with JSON responses

## 📁 Project structure

```
src/main/java/org/example/
├── BicycleRentalApplication.java
├── client/
│   ├── CepClient.java
│   └── viacep/ViaCepClient.java
├── config/
│   └── InitialDataLoader.java
├── controller/
│   └── *Controller.java
├── dto/
│   └── ApiDtos.java
├── handler/
│   └── GlobalExceptionHandler.java
├── domain/
│   ├── model/
│   │   ├── Address.java
│   │   ├── Administrator.java
│   │   ├── Bicycle.java
│   │   ├── Registrable.java
│   │   ├── Customer.java
│   │   ├── Employee.java
│   │   ├── Rental.java
│   │   ├── Payment.java
│   │   └── Reservation.java
│   ├── vo/
│   │   ├── Cep.java
│   │   ├── Cpf.java
│   │   └── Email.java
│   ├── enums/
│   │   ├── PaymentMethod.java
│   │   ├── BicycleStatus.java
│   │   ├── RentalStatus.java
│   │   └── ReservationStatus.java
│   └── exception/
│       ├── AddressLookupException.java
│       ├── InvalidCepException.java
│       ├── NotFoundException.java
│       ├── RentalException.java
│       ├── InvalidCpfException.java
│       └── InvalidEmailException.java
├── repository/
│   ├── BicycleRepository.java
│   ├── CustomerRepository.java
│   ├── RentalRepository.java
│   ├── ReservationRepository.java
│   └── inmemory/
│       ├── InMemoryBicycleRepository.java
│       ├── InMemoryCustomerRepository.java
│       ├── InMemoryRentalRepository.java
│       └── InMemoryReservationRepository.java
└── service/
    ├── AddressService.java
    ├── BicycleService.java
    ├── CustomerService.java
    ├── RentalService.java
    ├── PaymentService.java
    └── ReservationService.java

src/test/java/org/example/
├── client/viacep/ViaCepClientTest.java
└── service/
├── CustomerServiceTest.java
├── RentalServiceTest.java
├── PaymentServiceTest.java
└── ReservationServiceTest.java
```

## 🛠️ Technologies

- Java 25
- Maven
- JUnit 5

## 🚀 How to run

### Prerequisites

- JDK 25 installed
- Maven installed or Maven Wrapper configured

### Run the tests

```bash
mvn test
```

Or, if using the direct Maven path:

```powershell
& "C:\apache-maven-3.9.16\bin\mvn.cmd" test
```

### Run the application

```bash
mvn exec:java -Dexec.mainClass="org.example.application.Main"
```

## 📖 How to use

On startup, the system loads a few sample bicycles and shows the main menu.

Typical flow:

1. Register a customer
2. Make a reservation or rental
3. Report the return
4. Make the payment

## 📋 Business rules

- CPF must contain 11 numeric digits, ignoring dots and dashes
- Email must be valid
- CEP must contain 8 numeric digits (dash is ignored) and must exist in ViaCEP
- Only available bicycles can be reserved
- Only bicycles that are not rented or removed can be rented out
- Returns require hours greater than zero
- Payment is only allowed for finalized rentals

## 🏗️ Architecture notes

- `Cpf` and `Email` were modeled as Value Objects
- Statuses were converted into `enum`
- Repositories have interfaces and in-memory implementations
- `Main` only initializes the application and delegates execution to the console
- The structure is already prepared for a future migration to Spring Boot and JPA

## 👤 Author

Kauã Marinho

## 🚀 Running the API

```
./mvnw spring-boot:run
```

Endpoints (default port 8080, all under `/v1`): `GET/POST /v1/customers`, `GET /v1/bicycles`,
`GET/POST /v1/rentals`, `POST /v1/rentals/{id}/return`, `POST /v1/rentals/{id}/payment`,
`GET/POST /v1/reservations`, `POST /v1/reservations/{id}/cancel`, `GET /v1/addresses/{cep}`.

Registering a customer now requires a `cep`; the address is fetched from ViaCEP:

```json
POST /v1/customers
{ "name": "Ana", "cpf": "123.456.789-01", "email": "ana@email.com", "cep": "01310-100" }
```

CEP errors: invalid format → `400`, CEP not found → `404`, ViaCEP unreachable → `502`.
