# Movie-Ticket-booking-Application

A full-stack **Movie Ticket Booking Web Application** built with **Spring Boot** and **JWT Authentication**, supporting both **User** and **Admin** roles with real-time seat locking via **WebSocket**.
---
## 🚀 Live Features

### 👤 User Side
- Register & Login with JWT-based authentication
- Browse movies by location & theatre
- Select seats with real-time availability (WebSocket)
- Book tickets & make payments
- View booking history
- OTP-based login flow

### 🔐 Admin Side
- Secure Admin Login
- Add / Manage Movies
- View all bookings & offers
- Dashboard with full control

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java, Spring Boot |
| Security | JWT (JSON Web Token), Spring Security |
| Frontend | HTML, CSS, JavaScript |
| Database | MySQL |
| Real-time | WebSocket (STOMP) |
| ORM | Spring Data JPA / Hibernate |
| Build Tool | Maven |

---

## 📁 Project Structure

```
movie-ticket-booking/
├── src/main/java/com/example/cinema/
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   └── WebSocketConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── MovieController.java
│   │   ├── BookingController.java
│   │   ├── AdminController.java
│   │   ├── AdminMovieController.java
│   │   └── SeatWebSocketController.java
│   ├── entity/
│   │   ├── User.java
│   │   ├── Admin.java
│   │   ├── Movie.java
│   │   ├── Booking.java
│   │   ├── Offer.java
│   │   └── SeatLock.java
│   ├── repository/
│   ├── service/
│   └── security/
│       ├── JwtUtil.java
│       └── JwtFilter.java
└── src/main/resources/
    └── static/
        ├── auth/         # Login, OTP, Username pages
        ├── admin/        # Admin login & dashboard
        ├── booking/      # Seats, Movies, Theatre, Payment, etc.
        ├── css/
        └── js/
```

---

## ⚙️ How to Run Locally

### Prerequisites
- Java 17+
- MySQL
- Maven
- Spring Tool Suite (STS) or IntelliJ IDEA

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/Sai-Vijaya-Krishna/Movie-Ticket-booking-Application.git
cd Movie-Ticket-booking-Application/movie-ticket-booking
```

**2. Create MySQL Database**
```sql
CREATE DATABASE cinema_db;
```

**3. Configure application.properties**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cinema_db
spring.datasource.username=root
spring.datasource.password=your_password_here
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

**4. Run the application**
```bash
mvn spring-boot:run
```

**5. Open in browser**
```
http://localhost:8080
```

---

## 🔐 Authentication Flow

```
User/Admin  →  Login  →  JWT Token Generated
     ↓
Token stored in browser
     ↓
Every API request sends token in header
     ↓
JwtFilter validates token  →  Access granted/denied
```

---

## 📸 Pages Overview

| Page | Path |
|------|------|
| Home / Landing | `/index.html` |
| User Login/Register | `/auth/auth.html` |
| OTP Verification | `/auth/otp.html` |
| Select Location | `/booking/location.html` |
| Browse Movies | `/booking/movies.html` |
| Select Theatre | `/booking/theatre.html` |
| Select Seats | `/booking/seats.html` |
| Payment | `/booking/payment.html` |
| Booking Success | `/booking/success.html` |
| Booking History | `/booking/history.html` |
| Admin Login | `/admin/admin-login.html` |
| Admin Dashboard | `/admin/admin.html` |

---

## 🌟 Key Highlights for Recruiters

- ✅ **JWT Security** — Stateless authentication with token-based access control
- ✅ **Role-based Access** — Separate flows for User and Admin
- ✅ **WebSocket Integration** — Real-time seat locking to prevent double booking
- ✅ **RESTful APIs** — Clean controller-service-repository architecture
- ✅ **Spring Security** — Configured security filter chain with JWT filter
- ✅ **Full Stack** — End-to-end implementation from database to UI

---
## 👨‍💻 Developer
**Sai Vijaya Krishna**

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
