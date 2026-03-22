<div align="center">

# 🍽️ Food Order Management System

### A production-ready REST API for managing food orders end-to-end

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

</div>

---

## 📌 About

The **Food Order Management System (FMS)** is a RESTful backend application that handles the full lifecycle of a food order — from the moment a customer places it, through kitchen preparation, all the way to delivery or cancellation.

It includes:
- A **Spring Boot REST API** with 7 endpoints covering full CRUD + status management
- **Input validation** so bad data never reaches the database
- A **Global Exception Handler** that always returns clean, structured JSON errors
- **Smart status transitions** that enforce real-world order logic
- A **Swagger UI** to test every endpoint interactively in the browser
- A **modern dark-theme frontend** built in plain HTML/CSS/JS

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4 |
| Database | MySQL 8, Spring Data JPA |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven, Lombok |
| Frontend | HTML5, CSS3, JS |

---

## 📁 Project Structure

```
FoodOrderManagementSystem/
│
├── src/main/java/com/fms/
│   │
│   ├── 📂 config/
│   │   └── SwaggerConfig.java              # OpenAPI metadata & server info
│   │
│   ├── 📂 controller/
│   │   └── OrderOperationsController.java  # All 7 REST endpoints
│   │
│   ├── 📂 dto/
│   │   ├── ErrorResponse.java              # Structured error JSON shape
│   │   ├── OrderResponseDTO.java           # What the API returns to clients
│   │   └── OrderStatusUpdateRequest.java   # Request body for PATCH status
│   │
│   ├── 📂 entity/
│   │   ├── Order.java                      # JPA entity — maps to `orders` table
│   │   └── OrderItem.java                  # Embeddable — stores inside Order
│   │
│   ├── 📂 enums/
│   │   └── OrderStatus.java                # PENDING, CONFIRMED, PREPARING, DELIVERED, CANCELLED
│   │
│   ├── 📂 exception/
│   │   ├── GlobalExceptionHandler.java     # @RestControllerAdvice — catches all errors
│   │   ├── OrderNotFoundException.java     # Thrown when order ID doesn't exist
│   │   └── InvalidStatusTransitionException.java  # Thrown on illegal state change
│   │
│   ├── 📂 repository/
│   │   └── IOrderRepository.java           # JpaRepository + findByStatus()
│   │
│   ├── 📂 service/
│   │   ├── IOrderService.java              # Service interface
│   │   └── OrderServiceImpl.java           # Business logic implementation
│   │
│   └── FoodOrderManagementSystemApplication.java
│
├── src/main/resources/
│   ├── static/
│   │   └── index.html                      # Frontend UI (served by Spring Boot)
│   └── application.properties
│
└── pom.xml
```


---

## ⚙️ Getting Started

**Prerequisites:** Java 21+, Maven 3.9+, MySQL 8+

**1. Create the database**
```sql
CREATE DATABASE db_fms;
```

**2. Configure `application.properties`**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db_fms
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=2323

springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.try-it-out-enabled=true
```

**3. Run**
```bash
./mvnw spring-boot:run
```

| | URL |
|---|---|
| 🖥️ Frontend | http://localhost:2323 |
| 📖 Swagger UI | http://localhost:2323/swagger-ui.html |
| 🔌 API | http://localhost:2323/orders |

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/orders` | Place a new order |
| `GET` | `/orders` | Get all orders |
| `GET` | `/orders/{id}` | Get order by ID |
| `PUT` | `/orders/{id}` | Update order details |
| `PATCH` | `/orders/{id}/status` | Advance order status |
| `GET` | `/orders/status/{status}` | Filter orders by status |
| `DELETE` | `/orders/{id}` | Delete an order |

---

## 🔄 Order Status Lifecycle

Every order starts as `PENDING` and can only move **forward** — no skipping, no going back.

```
PENDING ──→ CONFIRMED ──→ PREPARING ──→ DELIVERED  (terminal)
   │               │              │
   └───────────────┴──────────────┴──→ CANCELLED   (terminal)
```

| From | Allowed Transitions |
|---|---|
| `PENDING` | `CONFIRMED`, `CANCELLED` |
| `CONFIRMED` | `PREPARING`, `CANCELLED` |
| `PREPARING` | `DELIVERED`, `CANCELLED` |
| `DELIVERED` | ❌ None |
| `CANCELLED` | ❌ None |

> Invalid transitions return **409 Conflict**

---

## 📦 Example

**POST /orders**
```json
{
  "customerName": "Som Gupta",
  "items": [
    { "productName": "Chicken Biryani", "quantity": 2, "price": 180.00 },
    { "productName": "Coke",           "quantity": 1, "price": 60.00  }
  ]
}
```

**Response — 201 Created**
```json
{
  "id": 1,
  "customerName": "Som Gupta",
  "status": "PENDING",
  "items": [...],
  "totalAmount": 420.0
}
```

> `totalAmount` is calculated on every response as `Σ (price × quantity)` — never stored in DB.

**PATCH /orders/1/status**
```json
{ "status": "CONFIRMED" }
```

---

## ⚠️ Error Responses

All errors return the same consistent JSON shape:

```json
// 400 — Validation failed
{
  "status": 400,
  "error": "Validation Failed",
  "message": "One or more fields have invalid values",
  "path": "/orders",
  "timestamp": "2026-03-23T10:45:00",
  "fieldErrors": [
    { "field": "customerName", "rejectedValue": "", "message": "must not be blank" }
  ]
}

// 404 — Order not found
{
  "status": 404,
  "error": "Order Not Found",
  "message": "Order not found with ID: 999",
  "path": "/orders/999",
  "timestamp": "2026-03-23T10:45:01"
}

// 409 — Invalid status transition
{
  "status": 409,
  "error": "Invalid Status Transition",
  "message": "Cannot transition order status from DELIVERED to PENDING",
  "path": "/orders/1/status",
  "timestamp": "2026-03-23T10:45:02"
}
```

---

## 🖥️ Frontend

A fully functional single-page frontend is served directly by Spring Boot from `src/main/resources/static/index.html` — no separate server, no npm, no build step.

**Features:**
- Dark glassmorphism design with animated aurora background
- Live dashboard — total orders, pending, in-progress, revenue
- Orders table with status filter pills and sidebar quick-filters
- Create, edit, view and delete orders via modals
- Click-to-advance status flow inside the order detail panel
- Live server connection indicator + toast notifications

Since the frontend is served from the same origin as the API (`localhost:2323`), **no CORS configuration is needed**.

---

## 📖 Swagger UI

Auto-generated from controller annotations. Access at:

```
http://localhost:2323/swagger-ui.html
```

Click **Try it out** on any endpoint to fire real API calls directly from the browser. Pre-filled example request bodies are included for every endpoint.

---

## 🧪 Test Checklist

- [x] `POST /orders` with valid body → 201 with correct `totalAmount`
- [x] `POST /orders` with blank name → 400 with `fieldErrors`
- [x] `POST /orders` with empty items → 400
- [x] `GET /orders` → returns all orders
- [x] `GET /orders/9999` → 404
- [x] `GET /orders/status/PENDING` → only pending orders
- [x] `PATCH` valid transition PENDING → CONFIRMED → 200
- [x] `PATCH` invalid transition CONFIRMED → PENDING → 409
- [x] `PATCH` on DELIVERED order → 409
- [x] `DELETE /orders/1` → success message

---

## 📄 License

This project is licensed under the **MIT License**.
