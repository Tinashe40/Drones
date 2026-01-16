# Drones Backend Application

This is a Spring Boot application that provides a REST API for managing a fleet of drones used for medication delivery.

## Features

The service allows:
*   Registering a drone.
*   Loading a drone with medication items.
*   Checking loaded medication items for a given drone.
*   Checking available drones for loading.
*   Checking drone battery level for a given drone.
*   Includes a periodic task to check drone battery levels and log audit events.
*   Prevents loading drones with excessive weight or low battery (<25%).

## Technologies Used

*   Spring Boot 3.x
*   Java 17
*   Maven
*   Spring Data JPA
*   H2 Database (in-memory)
*   Springdoc OpenAPI (for Swagger UI)

## Getting Started

To run this project on your machine, follow these steps:

### Prerequisites

Ensure you have the following installed:
*   **Java Development Kit (JDK) 17 or higher**
*   **Apache Maven 3.x**

### Setup and Run

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd drones-backend
    ```

2.  **Build the application:**
    This command compiles the code, runs tests, and packages the application into a JAR file.
    ```bash
    mvn clean install
    ```

3.  **Run the application:**
    This will start the Spring Boot application. It uses an in-memory H2 database, so no external database setup is required.
    ```bash
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8080` by default.

### API Documentation

Once the application is running, you can access the interactive API documentation (Swagger UI) at:
`http://localhost:8080/swagger-ui.html`

### H2 Database Console

You can access the in-memory H2 database console at:
`http://localhost:8080/h2-console`
Use the following credentials:
*   **JDBC URL:** `jdbc:h2:mem:dronedb`
*   **User Name:** `sa`
*   **Password:** (leave blank)

### Running Tests

To execute all unit and integration tests:
```bash
mvn test
```