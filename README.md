# SmartCampusAPI

### Name : B A D R Senarathne
### Student ID : 20232126/w2120666

## Overview

SmartCampusAPI is a RESTful web service built using JAX-RS (Jersey) and Apache Tomcat for the University of Westminster's "Smart Campus" initiative. The API manages Rooms and Sensors across campus, and maintains historical Sensor Readings. It is designed following REST architectural principles with proper resource hierarchy, HTTP status codes, error handling, and request/response logging.

# Video Demonstration 
https://drive.google.com/file/d/1ByvFzPJo_jMiiZCu-X2sUczCETKxbsKw/view?usp=sharing

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

# Conceptual Report (Q&A)

## Part 1: Service Architecture & Setup  

### Question 01 

JAX-RS creates a new instance of resources for every incoming HTTP request. Known as the 
per-request lifecycle. The instance variables inside a resource class are not shared between 
requests and cannot be used to store long-lived data. 
Since each request creates a new resource instance, all persistent data is discarded immediately.  
All the shared state is maintained in a centralized instance (DataStore), increasing the 
application’s lifetime. Also, ensure the thread is safe for concurrent access by using Concurrent 
HashMaps instead of normal HashMaps. These methods prevent data loss or race conditions. 

### Question 02 

Hypermedia as the Engine of Application State (HATEOAS) is a constraint on where API 
responses include links. Those guides users available actions , rather than user constructing 
URLs manually. 
This approach is beneficial for developers when compared to static documentation. Because API 
becomes self-discoverable. Users do not need to memories or hardcode the URL structures, 
simply follow the links provided in the responses. This makes the process easier and reduces the 
risk of errors from outdated documentation. 

## Part 2: Room Management 

### Question 01 

Returning only IDs reduces bandwidth usage and response size, However this way the client has 
to make additional requests to retrieve actual room data, which increases the number of HTTP 
round trips and adds latency. Mainly, when it comes to large data sets.  
Returning full room object increases the response size, but it gives all the details in a single 
request. Reducing round trips and keeping client side logic simple. For this particular scenario, a 
campus management API where the client needs all the data, returning full room objects would 
be most suitable. 
The choice depends on the use cases, the collection size, and the datasets. 

### Question 02 

The Delete operation is Idempotent in this implementation. If the Delete request to delete a 
room, if the room has no sensors, the request will be successful. But the second time the request 
will return an HTTP 404 Not Found code, since it was deleted by the first request and there is 
nothing else to delete for that room ID. This satisfies the definition of Idempotent, because the 
repeated request won't have any additional effect after the first request is successful. 

## Part 3: Sensor Operations & Linking  

### Question 01 

The @Consumes(MediaType.APPLICATION_JSON) annotation makes sure that JAX-RS only 
accepts requests with a content-type of application/json, If the request has any other content type, 
such as plain text or application/xml, JAX-RS will automatically reject the request before it even 
reaches the method body. The framework will give the HTTP 415 Unsupported Media Type 
response. This protects the method from data it cannot reserialize, preventing unexpected errors. 

### Question 02 

Path parameters are used to identify specific resources or a defined hierarchy, whereas query 
parameters are better suited for optional modifiers such as searching and sorting. 
Query parameters are semantically correct for optional filtering and searching of a collection. 
The base path /api/v1/sensors still represents the full collection of sensors, and the query 
parameter narrows the result without changing the identity of the resource being addressed. 
Using @QueryParam for filtering is considered superior to embedding the filter in the path for 
these reasons. 
Embedding the filter in the URL path implies that each variation is a distinct resource, which 
causes misleading architecture and Rigid URLs messy with path parameters. In contrast, 
@QueryParam handles multiple filters cleanly and naturally. 

## Part 4: Deep Nesting with Sub- Resources 

### Question 01 

The Sub-Resource Locator pattern enables a resource class to delegate the management of a 
subpath of the resource to another class that manages only that specific subresource. For this 
particular project, SensorResource manages the "/api/v1/sensors" path, whereas it delegates the 
"/api/v1/sensors/sensorId}/readings" path to the SensorReadingResource class. 
Using the Sub-Resource Locator pattern brings a series of advantages to the system architecture. 
Firstly, it ensures that a particular class manages only one specific resource. Indeed, 
SensorResource will handle only sensor resources, while SensorReadingResource will handle 
only sensor reading resources. Secondly, it prevents a single large controller class from handling 
all the nested paths of the system's resources. As the system grows larger and more paths need to 
be added, it becomes increasingly difficult to manage a single controller class. 

## Part 5: Advanced Error Handling, Exception Mapping & Logging  

### Question 01 

HTTP Status 404 Not Found should mean that the server is unable to find the resource 
corresponding to the requested URL. If a client attempts to POST a new sensor where the 
specified roomId does not exist in the database, then the URL itself (/api/v1/sensors) is entirely 
legitimate and the server finds no issue there. A status code of HTTP 422 Unprocessable Entity 
would be more appropriate for the above case 
since it suggests that the server successfully understood the client's request, the data passed in the 
message body are in an acceptable format, and the URL is valid, but the server cannot process 
this data due to some inherent errors present in the JSON body of the request. 
In this case, such a response code helps to clearly identify the source of the problem and where 
the client needs to focus to resolve the issue. 

### Question 02 

Providing visibility into internal Java stack traces to API consumers outside the system raises 
significant cybersecurity concerns. The internal details of the application exposed by stack traces 
include the names of classes, methods, lines, libraries, and their versions. The potential uses of 
this information by attackers in malicious activities are diverse. 
Firstly, they will be able to identify which third-party libraries and versions are used in the 
application development process and build tailored exploits. Secondly, class and method names 
reveal details about the application's internal workings, allowing attackers to craft input values 
that generate new errors. 
By using a global ExceptionMapper that intercepts all unhandled exceptions and returns only a 
generic 500 message, the API ensures no internal details are leaked to the outside world, while 
still logging the full stack trace internally. 

### Question 03 

Initially, a filter will be automatically applied to all requests and responses without requiring any 
changes to the resource class. This ensures that logging is applied consistently across all 
resources. If it were implemented directly into the methods, a programmer would forget to 
implement it in a newly created method. 
Secondly, filters support the concept of Separation of Concerns, which holds that the resource 
method should have no connection to other concerns and should contain only pure business 
logic. By implementing logging directly in the resource method, a programmer violates the 
principle of clean code and makes it harder to read. Using filters allows modifying the logging 
behaviour from one place and does not require changes to the resource class.

