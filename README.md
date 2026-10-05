# 🚲 Bicycle Rental System

![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)
![Maven](https://img.shields.io/badge/Maven-Build-blue?logo=apachemaven)
![JUnit5](https://img.shields.io/badge/Tests-JUnit5-25A162?logo=junit5)

A Spring Boot REST API for managing bicycle rentals, including customer registration, reservations, rentals, returns, and payments.

The project is organized to cleanly separate the console interface, the domain, and the in-memory infrastructure, and already includes automated tests for the services.

## ✨ Features

- Customer registration with CPF and email validation
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
├── controller/
│   ├── *Controller.java
│   ├── GlobalExceptionHandler.java
│   ├── InitialDataLoader.java
│   └── dto/ApiDtos.java
├── domain/
│   ├── model/
│   │   ├── Administrator.java
│   │   ├── Bicycle.java
│   │   ├── Registrable.java
│   │   ├── Customer.java
│   │   ├── Employee.java
│   │   ├── Rental.java
│   │   ├── Payment.java
│   │   └── Reservation.java
│   ├── vo/
│   │   ├── Cpf.java
│   │   └── Email.java
│   ├── enums/
│   │   ├── PaymentMethod.java
│   │   ├── BicycleStatus.java
│   │   ├── RentalStatus.java
│   │   └── ReservationStatus.java
│   └── exception/
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
    ├── BicycleService.java
    ├── CustomerService.java
    ├── RentalService.java
    ├── PaymentService.java
    └── ReservationService.java

src/test/java/org/example/service/
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

Endpoints (default port 8080): `GET/POST /customers`, `GET /bicycles`, `GET/POST /rentals`,
`POST /rentals/{id}/return`, `POST /rentals/{id}/payment`, `GET/POST /reservations`, `POST /reservations/{id}/cancel`.
