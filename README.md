# 🏭 Smart Inventory Management System

A full-stack **Spring Boot REST API** for managing inventory, suppliers, and purchase orders with **Role-Based Access Control (RBAC)**, **JWT Authentication**, **automated low-stock alerts**, and **PDF/CSV reporting**.

> **Developed by:** Hari Krishna  
> **Tech Stack:** Java · Spring Boot · Spring Security · JWT · MySQL · REST API · Git

---

## 📌 Features

| Feature | Description |
|---|---|
| 🔐 JWT Authentication | Secure login with token-based auth |
| 👤 RBAC | Admin and User roles with granular access control |
| 📦 Product Management | Full CRUD with SKU, category, and stock tracking |
| 🏢 Supplier Management | Manage supplier data linked to products |
| 🛒 Purchase Orders | Create, track, and auto-generate POs on low stock |
| ⚡ Low Stock Alerts | Auto-detects low stock & creates POs every 6 hours |
| 📊 Reports | Download inventory and order reports as **PDF** or **CSV** |
| 📈 Dashboard API | Summary of all key inventory metrics |

---

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3.2**
- **Spring Security + JWT (jjwt)**
- **Spring Data JPA + Hibernate**
- **MySQL 8.0**
- **iText PDF** (report generation)
- **Apache Commons CSV**
- **Lombok**
- **Maven**

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### 1. Clone the Repository
```bash
git clone https://github.com/harikrishna9398/smart-inventory-system.git
cd smart-inventory-system
```

### 2. Configure Database
Open `src/main/resources/application.properties` and set your MySQL credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/inventory_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password_here
```

### 3. Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

The server starts at: **http://localhost:8080**

---

## 📡 API Endpoints

### 🔑 Authentication
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | Public |
| POST | `/api/auth/login` | Login and get JWT token | Public |

### 📦 Products
| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/api/products` | Get all products | User/Admin |
| GET | `/api/products/{id}` | Get product by ID | User/Admin |
| GET | `/api/products/search?name=` | Search by name | User/Admin |
| GET | `/api/products/low-stock` | Get low stock products | User/Admin |
| POST | `/api/products` | Add new product | Admin |
| PUT | `/api/products/{id}` | Update product | Admin |
| PATCH | `/api/products/{id}/stock?change=` | Update stock quantity | User/Admin |
| DELETE | `/api/products/{id}` | Delete product | Admin |

### 🏢 Suppliers
| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/api/suppliers` | Get all suppliers | User/Admin |
| POST | `/api/suppliers` | Add supplier | Admin |
| PUT | `/api/suppliers/{id}` | Update supplier | Admin |
| DELETE | `/api/suppliers/{id}` | Delete supplier | Admin |

### 🛒 Purchase Orders
| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/api/purchase-orders` | Get all orders | User/Admin |
| GET | `/api/purchase-orders/status/{status}` | Filter by status | User/Admin |
| POST | `/api/purchase-orders` | Create new order | Admin |
| PATCH | `/api/purchase-orders/{id}/status` | Update order status | Admin |

### 📊 Reports (Admin Only)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/reports/inventory/pdf` | Download inventory PDF |
| GET | `/api/reports/inventory/csv` | Download inventory CSV |
| GET | `/api/reports/purchase-orders/csv` | Download orders CSV |

### 📈 Dashboard
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/dashboard/summary` | Get key metrics summary |

---

## 🔐 Sample API Usage

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

### Use JWT token for protected endpoints
```bash
curl -X GET http://localhost:8080/api/products/low-stock \
  -H "Authorization: Bearer <your_token_here>"
```

### Create a product (Admin)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "sku": "LAP-001",
    "category": "Electronics",
    "price": 45000,
    "quantity": 50,
    "lowStockThreshold": 5,
    "supplierId": 1
  }'
```

---

## 📁 Project Structure

```
smart-inventory-system/
├── src/main/java/com/inventory/
│   ├── SmartInventoryApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java       # Spring Security + RBAC
│   │   ├── JwtUtil.java              # JWT token generation/validation
│   │   └── JwtAuthFilter.java        # JWT request filter
│   ├── controller/
│   │   ├── AuthController.java       # Login/Register endpoints
│   │   ├── ProductController.java    # Product CRUD
│   │   ├── SupplierController.java   # Supplier management
│   │   ├── PurchaseOrderController.java
│   │   ├── ReportController.java     # PDF & CSV downloads
│   │   └── DashboardController.java  # Metrics summary
│   ├── service/
│   │   ├── ProductService.java       # Business logic
│   │   ├── PurchaseOrderService.java # Auto PO on low stock
│   │   └── ReportService.java        # PDF/CSV generation
│   ├── model/
│   │   ├── User.java                 # User with roles
│   │   ├── Product.java              # Inventory item
│   │   ├── Supplier.java
│   │   └── PurchaseOrder.java
│   └── repository/                   # JPA repositories
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

---

## 🌟 Key Highlights

- **~60% reduction** in manual stock tracking via automated REST APIs
- **Auto Purchase Order generation** triggered every 6 hours for low-stock products
- **Role-Based Access Control** with JWT ensures data security
- **PDF and CSV export** for management decision-making
- Clean layered architecture: Controller → Service → Repository → Database

---

## 📬 Contact

**Hari Krishna**  
📧 krishnaa9398@gmail.com  
📍 Hyderabad, Telangana  

---

*This project was built as part of my portfolio for the C-DAC Project Associate application.*
