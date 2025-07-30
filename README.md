# UPI Backend System using Java & Spring Boot

A comprehensive backend system for UPI (Unified Payment Interface) built with Java and Spring Boot.

## Features

- User authentication and authorization with JWT
- Bank account management
- Virtual Payment Address (VPA) management
- Transaction processing
- Role-based access control
- API documentation with Swagger/OpenAPI

## Technology Stack

- Java 17
- Spring Boot 2.7.x
- Spring Security
- Spring Data JPA
- PostgreSQL (with H2 for testing)
- JWT for authentication
- Lombok for reducing boilerplate code
- ModelMapper for DTO conversions
- Swagger/OpenAPI for API documentation

## Project Structure

```
src/main/java/com/upi/
├── config/                  # Configuration classes
├── controller/              # REST controllers
├── dto/                     # Data Transfer Objects
│   ├── auth/                # Authentication DTOs
│   ├── bank/                # Bank account DTOs
│   ├── transaction/         # Transaction DTOs
│   └── vpa/                 # VPA DTOs
├── exception/               # Exception handling
├── model/                   # Entity classes
├── repository/              # JPA repositories
├── security/                # Security configuration
│   ├── jwt/                 # JWT utilities
│   └── services/            # Security services
├── service/                 # Service interfaces
│   └── impl/                # Service implementations
└── util/                    # Utility classes
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher

### Database Setup

1. Create a PostgreSQL database named `upidb`:

```sql
CREATE DATABASE upidb;
```

2. Update the database configuration in `application.properties` if needed:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/upidb
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Building and Running

1. Clone the repository:

```bash
git clone https://github.com/Github-Saurabh0/Design-a-backend-for-upi-using-Java.git
cd Design-a-backend-for-upi-using-Java
```

2. Build the project:

```bash
mvn clean install
```

3. Run the application:

```bash
mvn spring-boot:run
```

The application will start on port 8080 by default.

## API Documentation

Swagger UI is available at: http://localhost:8080/swagger-ui.html

API docs are available at: http://localhost:8080/api-docs

## Authentication

### Register a new user

```
POST /api/auth/signup
```

Request body:
```json
{
  "username": "johndoe",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "password": "password123",
  "roles": ["user"]
}
```

### Login

```
POST /api/auth/signin
```

Request body:
```json
{
  "username": "johndoe",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "username": "johndoe",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "roles": ["ROLE_USER"]
}
```

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