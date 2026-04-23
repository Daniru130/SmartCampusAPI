# SmartCampusAPI

## Overview

SmartCampusAPI is a RESTful web service built using JAX-RS (Jersey) and Apache Tomcat for the University of Westminster's "Smart Campus" initiative. The API manages Rooms and Sensors across campus, and maintains historical Sensor Readings. It is designed following REST architectural principles with proper resource hierarchy, HTTP status codes, error handling, and request/response logging.

### Key Features
- Room management (create, retrieve, delete with safety checks)
- Sensor management (create, retrieve, filter by type)
- Sensor readings history (append and retrieve per sensor)
- Custom exception handling (409, 422, 403, 500)
- Request and response logging via JAX-RS filters
- In-memory data storage using ConcurrentHashMap

### Base URL
http://localhost:8080/SmartCampusAPI/api/v1

### Resource Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/v1 | Discovery endpoint |
| GET | /api/v1/rooms | Get all rooms |
| POST | /api/v1/rooms | Create a new room |
| GET | /api/v1/rooms/{roomId} | Get a specific room |
| DELETE | /api/v1/rooms/{roomId} | Delete a room |
| GET | /api/v1/sensors | Get all sensors (optional ?type= filter) |
| POST | /api/v1/sensors | Create a new sensor |
| GET | /api/v1/sensors/{sensorId} | Get a specific sensor |
| GET | /api/v1/sensors/{sensorId}/readings | Get all readings for a sensor |
| POST | /api/v1/sensors/{sensorId}/readings | Add a new reading for a sensor |

---

## How to Build and Run

### Prerequisites
- Java JDK 11 or higher
- Apache Maven 3.6+
- Apache Tomcat 9
- NetBeans IDE (recommended) or any Maven-compatible IDE

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/YOUR_USERNAME/SmartCampusAPI.git
```

**2. Open in NetBeans**
- File → Open Project → select the cloned folder

**3. Build the project**
- Right-click the project → Clean and Build
- This will generate `SmartCampusAPI.war` inside the `target/` folder

**4. Run the project**
- Right-click the project → Run
- NetBeans will deploy it to Tomcat automatically

**5. Access the API**
- Open your browser or Postman
- Go to: `http://localhost:8080/SmartCampusAPI/api/v1`

---

## Sample curl Commands

### 1. Discovery endpoint
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1
```

### 2. Get all rooms
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

### 3. Create a new room
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"HALL-01\",\"name\":\"Main Hall\",\"capacity\":200}"
```

### 4. Get all sensors filtered by type
```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=Temperature"
```

### 5. Create a new sensor linked to a room
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"OCC-001\",\"type\":\"Occupancy\",\"status\":\"ACTIVE\",\"currentValue\":0,\"roomId\":\"LIB-301\"}"
```

### 6. Add a sensor reading
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d "{\"value\":24.3}"
```

### 7. Get all readings for a sensor
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings
```

### 8. Try to delete a room that still has sensors (expects 409 Conflict)
```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301
```

### 9. Try to create a sensor with a non-existent room (expects 422)
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"BAD-001\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":0,\"roomId\":\"FAKE-999\"}"
```

---

## Technology Stack
- Java 11
- JAX-RS via Jersey 2.41
- Apache Tomcat 9
- Jackson (JSON serialization)
- Maven (build tool)
- In-memory storage (ConcurrentHashMap)

---

