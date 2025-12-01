
# Inventory Reservation Service

A Spring Boot microservice responsible for product inventory reservations.  
It exposes REST APIs to create and confirm reservations, manages stock, prevents overselling using optimistic locking, and automatically expires old reservations using a scheduler. All stock operations are protected using JWT authentication.

---

## 🌟 Features & Acceptance Criteria

- **Create reservation API → sets status to `PENDING`**
- **Stock decreases only after reservation confirmation**
- **Validation error when stock is insufficient**
- **Prevent overselling using optimistic locking**
- **Scheduler expires old reservations and restores stock**
- **JWT authentication for all stock operations**
- **Structured SLF4J logs for reservation + confirmation + expiry**

---

## 🛠 Tech Stack

- Java 17  
- Spring Boot 4.x  
- Spring Web  
- Spring Data JPA  
- Spring Security (JWT)  
- MySQL  
- Maven  
- Lombok  
- SLF4J  

---

## Project Structure

```text
src/main/java/com/example/inventory
├── InventoryReservationApplication.java
├── model
│   ├── Product.java
│   ├── InventoryReservation.java
│   └── ReservationStatus.java
├── dto
│   ├── CreateReservationRequest.java
│   ├── ReservationResponse.java
│   ├── ConfirmReservationResponse.java
│   └── ErrorResponse.java
├── repository
│   ├── ProductRepository.java
│   └── InventoryReservationRepository.java
├── service
│   └── InventoryService.java
├── scheduler
│   └── ReservationExpiryScheduler.java
├── controller
│   ├── InventoryController.java
│   └── AuthController.java
├── security
│   ├── AuthRequest.java
│   ├── AuthResponse.java
│   ├── JwtUtil.java
│   ├── JwtAuthFilter.java
│   ├── CustomUserDetailsService.java
│   └── SecurityConfig.java
└── exception
    └── GlobalExceptionHandler.java

## ⚙️ Configuration

`src/main/resources/application.properties`

```properties
spring.application.name=inventory-reservation

spring.datasource.url=jdbc:mysql://localhost:3306/inventorydb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

reservation.expiry.minutes=5

jwt.secret=ChangeThisSecretKeyForJWT123456789
jwt.expiration-ms=3600000

logging.level.com.example.inventory=INFO

▶️ Running the Application
Prerequisites

JDK 17+

Maven

MySQL running on localhost:3306

Steps
mvn clean install
mvn spring-boot:run


App runs at:
👉 http://localhost:8080

🔐 Authentication (JWT)

Default credentials:

username: admin
password: password

1️⃣ Get JWT Token

POST http://localhost:8080/auth/login

Body:

{
  "username": "admin",
  "password": "password"
}


Response:

{
  "token": "<JWT_TOKEN>"
}


Add this to every request:

Authorization: Bearer <JWT_TOKEN>

📡 REST APIs
1. Create / Update Product
POST /api/products

{
  "sku": "SKU-001",
  "name": "Demo Product",
  "availableStock": 100
}

2. Get Stock
GET /api/products/{sku}/stock

3. Create Reservation (PENDING)
POST /api/reservations

{
  "sku": "SKU-001",
  "quantity": 5
}

4. Confirm Reservation
POST /api/reservations/{id}/confirm

⏱ Scheduler & Expiry

Configured via:

reservation.expiry.minutes=5


Scheduler runs every 60 seconds to:

Find expired reservations

Mark them as EXPIRED

Restore stock

📜 Logging

Logs include:

Reservation creation

Reservation confirmation

Expired reservations

Scheduler execution

Centralized exception logging
