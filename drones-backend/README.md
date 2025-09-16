# Drones Backend Application

This is a Spring Boot application that manages a fleet of drones for medication delivery.

## Features

- Register a drone
- Load a drone with medication items
- Check loaded medication items for a given drone
- Check available drones for loading
- Check drone battery level for a given drone
- Periodic task to check drones battery levels and create history/audit event log

## Technologies Used

- Spring Boot 3.x
- Java 17
- Maven
- Spring Data JPA
- H2 Database (in-memory)
- Lombok
- Validation (Jakarta Bean Validation)

## Setup and Run

1.  **Prerequisites:**
    - Java Development Kit (JDK) 17 or higher
    - Maven 3.x

2.  **Clone the repository:**

    ```bash
    git clone <repository-url>
    cd drones-backend
    ```

3.  **Build the application:**

    ```bash
    mvn clean install
    ```

4.  **Run the application:**

    ```bash
    mvn spring-boot:run
    ```

    The application will start on port 8080 by default.

## H2 Database Console

Once the application is running, you can access the H2 database console at:

`http://localhost:8080/h2-console`

Use the following credentials:
-   **JDBC URL:** `jdbc:h2:mem:dronedb`
-   **User Name:** `sa`
-   **Password:** (leave blank)

## API Endpoints

All endpoints are prefixed with `/api/drones`.

### 1. Register a Drone

-   **URL:** `/api/drones`
-   **Method:** `POST`
-   **Request Body:**

    ```json
    {
        "serialNumber": "DRONE-006",
        "model": "LIGHTWEIGHT",
        "weightLimit": 100.0,
        "batteryCapacity": 95,
        "state": "IDLE"
    }
    ```

-   **Response:** `201 Created` and the registered Drone object.

### 2. Load a Drone with Medication Items

-   **URL:** `/api/drones/{serialNumber}/load`
-   **Method:** `POST`
-   **Request Body:**

    ```json
    {
        "medications": [
            {
                "name": "Paracetamol",
                "weight": 10.5,
                "code": "PARA_001",
                "imageUrl": "http://example.com/para.jpg"
            },
            {
                "name": "Aspirin",
                "weight": 5.0,
                "code": "ASP_002",
                "imageUrl": "http://example.com/asp.jpg"
            }
        ]
    }
    ```

-   **Response:** `200 OK` and the updated Drone object.

### 3. Check Loaded Medication Items for a Given Drone

-   **URL:** `/api/drones/{serialNumber}/medications`
-   **Method:** `GET`
-   **Response:** `200 OK` and a list of Medication objects.

### 4. Check Available Drones for Loading

-   **URL:** `/api/drones/available`
-   **Method:** `GET`
-   **Response:** `200 OK` and a list of available Drone objects (IDLE state, battery >= 25%).

### 5. Check Drone Battery Level for a Given Drone

-   **URL:** `/api/drones/{serialNumber}/battery`
-   **Method:** `GET`
-   **Response:** `200 OK` and the battery level (integer).

## Error Handling

-   **400 Bad Request:** For validation errors or business rule violations (e.g., exceeding weight limit, low battery).
-   **404 Not Found:** If a drone with the given serial number is not found.
-   **500 Internal Server Error:** For unexpected server errors.

## Audit Log

A scheduled task runs every 5 minutes to check the battery levels of all registered drones and logs an audit event. These events are stored in the `AUDIT_LOG` table in the H2 database.

## Initial Data

The application preloads some dummy drone data into the H2 database on startup from `src/main/resources/data.sql`.

## Testing

To run the tests, execute:

```bash
mvn test
```
