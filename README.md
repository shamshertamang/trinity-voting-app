# Trinity College Voting Application — Full Stack on Kubernetes

A two-service, containerized React + Spring Boot application deployed on Kubernetes (Docker Desktop). The React frontend talks to the Spring microservice via in-cluster service discovery.

---

## Table of Contents

- [Trinity College Voting Application — Full Stack on Kubernetes](#trinity-college-voting-application--full-stack-on-kubernetes)
  - [How To Use the Application](#how-to-use-the-application)
  - [About the application](#about-the-application)
    - [Architecture Overview](#architecture-overview)
    - [Features](#features)
  - [Repository Layout](#repository-layout)
  - [Prerequisites](#prerequisites)
  - [What You'll Deploy](#what-youll-deploy)
  - [Project Set up](#project-set-up)
  - [Raw Run (No Container, No Kubernetes)](#raw-run-no-container-no-kubernetes)
  - [Run with Container Orchestration (No DockerHub)](#run-with-container-orchestration-no-dockerhub)
  - [Run with Container Orchestration (using DockerHub)](#run-with-container-orchestration-using-dockerhub)
  - [Deploy to Kubernetes, Run and Clean Up](#deploy-to-kubernetes-run-and-clean-up-2)
  - [Test the Full Stack (CRUD)](#test-the-full-stack-crud)
  - [Location for Screenshots of Submission](#location-for-screenshots-of-submission)


---

## How To Use the Application

1. **Voter Authentication**: Users must enter a valid Trinity College email address
2. **Voting Options**:
    - Select from existing candidates in a dropdown menu
    - Type in a new candidate name to add them to the system
3. **Vote Processing**: Each vote is recorded and candidate vote counts are updated
4. **Candidate Pool**: New candidates are automatically added to the selection list for future voters

---

## About the application

### Architecture Overview

This microservice is part of a three-tier Kubernetes-deployed system:

| Layer | Technology                | Description |
|-------|---------------------------|-------------|
| Frontend | React + Vite  | User interface for students to cast, update, and delete votes. Communicates with the backend over RESTful APIs. |
| Backend | Spring Boot (Java)        | Handles candidate and vote persistence, validation, and API endpoints. |
| Database | H2 | Stores candidates and votes. |

All components are containerized and deployed as independent Kubernetes pods. The frontend communicates with the backend via the ```spring-service``` Kubernetes service name (DNS).

### Features

- **Email validation** — ensures only ```username@trincoll.edu``` emails can vote.

- **Vote management** — submit, update, or delete a vote tied to an email.

- **Live results** — dynamically fetch and render current vote counts.

- **Auto-detection** — detects if a user has already voted and displays editing/deletion options.

- **Automatic reloads** — results refresh after any submission or deletion.

- **Microservice isolation** — communicates via REST API proxied through NGINX to the backend.


---


## Repository Layout

```
trinity-voting-app/
├── README.md                 # (this file) end-to-end instructions
├── spring-app/               # Spring Boot microservice (REST CRUD + Swagger UI)
│   └── README.md             # backend-only developer guide
│   └── .mvn/wrapper
│   └── src/                  # backend source code
│   └── .gitattributes
│   └── .gitignore            # untracked files in gitignore
│   └── mvnw 
│   └── mvnw.cmd              
│   └── pom.xml               # dependency manager
├── react-app/                # React frontend microservice (Vite + NGINX)
│   └── README.md             # frontend-only developer guide
│   └── public/vite.svg
│   └── src/                  # backend source code
│   └── .dockerignore
│   └── .gitignore            # untracked files in gitignore
│   └── Dockerfile 
│   └── eslint.config.js              
│   └── index.html            
│   └── nginx.conf
│   └── package-lock.json              
│   └── package.json            
│   └── vite.config.js
└── kubernetes/               # K8s manifests
│   └── spring-deployment.yaml
│   └── spring-service.yaml
│   └── react-deployment.yaml
│   └── react-service.yaml
└── .gitignore
└── screenshots
```

---

## Prerequisites

- **Docker Desktop** with Kubernetes enabled
- **kubectl** in your PATH
- **DockerHub account** (for `docker push`)
- **Node.js 20+**
- **Java 21 + Maven**

> **Tip:** Make sure Docker Desktop shows "Kubernetes: Running".

---

## What You'll Deploy

### Spring microservice (2 replicas)
- REST endpoints under `/api/**`
- Swagger UI at `/swagger-ui`
- In-cluster DNS name: `spring-service:8080`

### React frontend (2 replicas)
- Built with Vite; served by NGINX
- Calls backend via `http://spring-service:8080/api/...`
- Exposed via `react-service` (NodePort or port-forward)

---

## Project Set up

```bash
   # Clone the repository
   git clone https://github.com/shamshertamang/trinity-voting-app.git
   cd trinity-voting-app
```

---

## Raw Run (No Container, No Kubernetes)

To run without the container and the kubernetes: 

### 1) Spring-App (Backend)

Go to the `spring-app` and find `Run Locally (no-containers)` in the `Table of Contents` of the `README.md` file and follow the instructions. This will initialise the backend.

### 2) React-App (Frontend)

Go to the `react-app` and find `Local Development and Quick Run` in the `Table of Contents` of the `README.md` file and follow the instructions. This will start the frontend and the application has started.

---

## Run with Container Orchestration (No DockerHub)

### 1) Raw Run

If you haven't done a raw run, follow the instructions in the section `Raw Run` of this `README.md` to make sure everything is running smoothly.

### 2) Spring-App (Backend) Image Creation

Go to the `spring-app` and find `Build and Run Container (Local- No DockerHub)` in the `Table of Contents` of the `README.md` file and follow the instructions.

### 3) React-App (Frontend) Image Creation

Go to the `react-app` and find `Local Build (No DockerHub)` in the `Table of Contents` of the `README.md` file and follow the instructions.

### 4) Configure Kubernetes Manifests

Open files in ```kubernetes/``` and confirm the image fields match what you pushed:

- ```spring-deployment.yaml``` → ```image: trinity-spring:local```
- ```react-deployment.yaml``` → ```image: react-frontend:local```

### 5) Deploy to Kubernetes, Run and Clean Up

Follow the instructions in the `Deploy to Kubernetes, Run and Clean Up` section of this `README.md`.

---

## Run with Container Orchestration (using DockerHub)

### 1) Raw Run

If you haven't done a raw run, follow the instructions in the section `Raw Run` of this `README.md` to make sure everything is running smoothly.

### 2) Spring-App (Backend) Image Creation

Go to the `spring-app` and find `Build and Run Container (DockerHub)` in the `Table of Contents` of the `README.md` file and follow the instructions.

### 3) React-App (Frontend) Image Creation

Go to the `react-app` and find `Build (DockerHub)` in the `Table of Contents` of the `README.md` file and follow the instructions.

### 4) Configure Kubernetes Manifests

Open files in ```kubernetes/``` and confirm the image fields match what you pushed:

- ```spring-deployment.yaml``` → ```image: <your-dockerhub-username>/trinity-spring:1.0.0```
- ```react-deployment.yaml``` → ```image: <your-dockerhub-username>/trinity-react:1.0.0```

### 5) Deploy to Kubernetes, Run and Clean Up

Follow the instructions in the `Deploy to Kubernetes, Run and Clean Up` section of this `README.md`.

---

## Deploy to Kubernetes, Run and Clean Up

```bash
    # -1) setup keyboard shortcut
    alias k=kubectl
    
    # 0) point k8s at docker desktop's cluster
    k config get-contexts
    k config use-context docker-desktop 
    k cluster-info
    
    # Step 1) deploy spring boot and react
    # make sure deployment.yaml files have correct images for spring-boot and react   
    k apply -f kubernetes/
    
    # Step 2) verify deployments and services are running
    k get deployments      
    k get services    
    k get all  
    # You should see:
      # 2 Deployments (spring, react)
      # 2 ReplicaSets
      # 4 Pods total (2 spring + 2 react)
      # 2 Services (spring-service, react-service) 
    
    # Step 3) Open the app at 
    http://localhost:5173
    
    # Step 4) Clean up
    k delete -f kubernetes/
```

---

## Test the Full Stack (CRUD)
From the React UI:

1. Enter a valid email like ```user@trincoll.edu```
2. **Create**: add a vote (choose existing or type a new candidate)
3. **Read**: results list shows candidate tallies
4. **Update**: edit your vote (email is your key)
5. **Delete**: remove your vote and confirm results update

Backend Swagger is also available at:
```bash
  kubectl port-forward svc/spring-service 8080:8080
```

then open ```http://localhost:8080/swagger-ui```.

---

## Location for Screenshots of Submission

```bash
    screenshots/
    ├── kubectl-get-all.png         # shows 2 Deployments, 2 RS, 4 Pods, 2 Services
    └── working-ui.png              # React UI with visible URL and successful CRUD
```