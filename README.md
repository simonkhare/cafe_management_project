# ☕ The Aroma Cafe HQ

A full-stack café management platform built with **Spring Boot, Thymeleaf, and MySQL**. The system uses session-based role routing to provide custom dashboard experiences for Administrators, Kitchen Staff, and Customers, wrapped in a sleek, responsive UI.

---

## 🛠️ Tech Stack

* **Backend:** Java 17, Spring Boot, Spring Data JPA
* **Database:** MySQL
* **Frontend:** Thymeleaf, Bootstrap 5, Bootstrap Icons, Poppins Font

---

## 🎯 Core Features

### 🏛️ Admin Hub

* **Revenue Tracking:** Real-time calculation of gross sales from completed orders.
* **Account Management:** Create secure employee credentials or revoke user access.
* **Menu Control:** Add or remove product variants with dynamic image upload support.

### 🧑‍🍳 Kitchen Matrix (Employee)

* **Shared Order Pool:** A real-time processing queue for all active incoming tickets.
* **Personalized History:** An isolated log allowing staff to track only the orders they personally fulfilled.

### 🛍️ Shopping Tray (Customer)

* **Interactive Cart:** Client-side JavaScript cart with live total and quantity updates.
* **Receipts Archive:** Immediate access to personal invoices and order status tracking.

---

## 🚀 Quick Setup

1. **Database:** Create a database named `cafe_management` in MySQL.
2. **Configure:** Update your database username and password in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cafe_management?useSSL=false&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

```


3. **Run:** Execute the application using Maven or launch it directly from your IDE:
```bash
mvn spring-boot:run

```


4. **Access:** Open `http://localhost:8080` in your web browser.
