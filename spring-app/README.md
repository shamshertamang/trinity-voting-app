# Trinity College Voting — Spring Backend Microservice

This directory contains the **backend microservice** for the Trinity College Voting System. It exposes a REST API for managing candidates and votes. The React frontend (in `../react-app`) consumes these endpoints.

---

## Table of Contents
- [Tech Stack](#tech-stack)
- [Run Locally (no-containers)](#run-locally-no-containers)
- [Build Jar](#build-jar)
- [Build and Run Container (Local- No DockerHub)](#build-and-run-container-local--no-dockerhub)
- [Build and Run Container (DockerHub)](#build-and-run-container-dockerhub)
- [API Overview](#api-overview)
- [Notes](#notes)

---

## Tech Stack

- **Spring Boot** (Java 21)
- **H2 in-memory DB** (dev/demo)
- **Maven** build
- **Docker** container
- **Swagger UI** at `/swagger-ui`

---

## Run Locally (no containers)
```bash
    # from spring-app/
    ./mvnw clean spring-boot:run
    
    # or
    mvn clean spring-boot:run
```

Open Swagger:
```bash
      http://localhost:8080/swagger-ui
```

---

## Build Jar

```bash
    ./mvnw clean package
    # jar at target/*.jar
```

---

## Build and Run Container (Local- No DockerHub)

### Build Container
```bash
    # On MacOS/Linux
    ./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=trinity-spring:local
    # On Windows
    mvnw.cmd spring-boot:build-image -Dspring-boot.build-image.imageName=trinity-spring:local
```

---

### Run Container
```bash
    docker run --rm -p 80:8080 trinity-spring:local
    
    docker run -d --name spring-service --network appnet -p 80:8080 trinity-spring:local

    
    # open http://localhost:80/swagger-ui
```

## Build and Run Container (DockerHub)

### Build & Push Container
```bash
    # MacOS/Linux
    ./mvnw spring-boot:build-image
    # Windows
    mvnw.cmd spring-boot:build-image
    
    # retag and push
    docker tag my-spring-app:v1.0.0 <your-dockerhub-username>/trinity-spring:1.0.0
    docker push <your-dockerhub-username>/trinity-spring:1.0.0
```

---

### Run Container Locally
```bash
    docker run --rm -p 80:8080 <your-dockerhub-username>/trinity-spring:1.0.0
    
    # open http://localhost:80/swagger-ui
```

---

## API Overview

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/candidates` | List candidates + vote counts |
| POST | `/api/vote` | Create a vote `{voterEmail, candidateName}` |
| PUT | `/api/vote` | Update a vote `{voterEmail, candidateName}` |
| DELETE | `/api/vote/{email}` | Delete vote for email |
| GET | `/api/votes/{email}` | Get vote by email (404 if none) |

Note: Email validation requires username@trincoll.edu (no extra dots before @).

---

## Notes

- **State**: H2 is in-memory by default (reset on restart). Swap to Postgres/MySQL for persistence if needed.
- **CORS**: Not required in-cluster; React talks to Spring via service DNS and the NGINX proxy.
- **Logging**: Use kubectl logs on pods for debugging.