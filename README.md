# UPI Backend System using Java & Spring Boot

This project is a full-fledged backend simulation of a UPI (Unified Payments Interface) system using **Java**, **Spring Boot**, and **PostgreSQL**. It covers essential features like user onboarding, bank account linking, VPA creation, P2P transactions, and transaction history.

> Note: This is a simulated backend intended for learning and prototyping. Real UPI integration requires approval from NPCI/RBI.

---

## Tech Stack

- **Java 17**
- **Spring Boot**
- **PostgreSQL**
- **Spring Security + JWT**
- **REST APIs**
- **Lombok**, **MapStruct**, **ModelMapper**
- **Swagger/OpenAPI** (for testing)
- **Maven**

---

## Modules Included

| Module               | Features Covered |
|----------------------|------------------|
| **User Service**     | Register/Login, JWT Auth |
| **Bank Service**     | Link bank, get balance |
| **VPA Service**      | Create/manage UPI IDs |
| **Transfer Service** | P2P money transfer |
| **Transaction Logs** | History, UTR, status |
| **Security**         | Encrypted UPI PIN, JWT-based auth |
| **Admin**            | Basic fraud protection (planned) |

---

## Endpoints (Sample)

| Method | Endpoint              | Description             |
|--------|------------------------|-------------------------|
| POST   | `/auth/register`       | Register new user       |
| POST   | `/auth/login`          | User login              |
| POST   | `/bank/link`           | Link a bank account     |
| POST   | `/upi/create-vpa`      | Generate a VPA ID       |
| POST   | `/upi/transfer`        | Transfer funds via VPA  |
| GET    | `/upi/history/{vpa}`   | Get transaction history |

---

## Security Features

- JWT Authentication
- Encrypted UPI PIN
- Role-based access control
- Input validation & exception handling

---

## How to Run Locally

### 1. Clone the repository
```bash
git clone https://github.com/Github-Saurabh0/Design-a-backend-for-upi-using-Java.git
cd Design-a-backend-for-upi-using-Java
```

### 2. Set up PostgreSQL database
Create a DB named `upi_backend` and update your `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/upi_backend
    username: postgres
    password: your_password
```

### 3. Run the application
```bash
./mvnw spring-boot:run
```

### 4. Access Swagger UI
```
http://localhost:8080/swagger-ui.html
```

---

## Folder Structure

```
src/main/java/com/upi
├── controller
├── service
├── model
├── dto
├── repository
├── security
├── util
└── config
```

---

## Features To Add (Upcoming)

- UPI QR code generation (`upi://pay`)
- UTR system & dispute handling
- WebSocket for real-time status
- NPCI sandbox/mock gateway integration
- Admin dashboard for logs/fraud
- Transaction rollback & retry mechanism

---

## Author

**Saurabh Kushwaha**  
🔗 [Portfolio](https://www.saurabhh.in)  
📧 Saurabh@wearl.co.in  
🔗 [LinkedIn](https://www.linkedin.com/in/saurabh884095/)  
🔗 [Instagram Dev Page](https://www.instagram.com/dev.wearl)

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Star this repo if you found it helpful!