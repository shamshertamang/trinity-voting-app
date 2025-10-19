# Trinity College Voting App — React Frontend

---

This directory contains the frontend microservice for the Trinity College Voting System, a secure and easy-to-use campus voting platform.
The frontend is built with **React + Vite**, styled with plain CSS, and served in production via **NGINX** with proxy rules to the Spring Boot backend service.

---

## Table of Contents
- [Local Development and Quick Run](#local-development-and-quick-run)
- [Docker Build & Run](#docker-build--run)
    - [Local Build (No DockerHub)](#local-build-no-dockerhub)
    - [Build (DockerHub)](#build-dockerhub)
- [Kubernetes Deployment](#kubernetes-deployment)
- [API Integration](#api-integration)
- [Project Structure](#project-structure)
- [Environment Variables](#environment-variables)
- [UI Components](#ui-components)
- [Developer Notes](#developer-notes)
- [License](#license)


---

## Local Development and Quick Run

1) Prerequisites

- Node.js 20+

- npm or yarn

- Backend service running locally (Spring Boot) — default on port **8080**

2) Installation
```bash
  # Install dependencies
  npm ci
```

3) Start the development server

The frontend runs locally on Vite and proxies /api/* calls to your backend:

```bash
  npm run dev
```

Access it at:
```bash
  http://localhost:5173
```

4) Configure backend target

The proxy target is set in **vite.config.js**:
```bash
  const BACKEND = process.env.BACKEND || 'http://localhost:8080';
```

---

## Docker Build & Run

The Dockerfile uses a **two-stage build** (Node build → NGINX serve):

```
# Build stage
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Serve stage
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### Local Build (No DockerHub)
Build and run locally:
```bash
    # Build the Docker container
    docker build -t react-frontend:local .
    
    # Run the container
    docker run --rm -p 3000:3000 react-frontend:local
```

Now visit
```bash
  http://localhost:5173
```
to see the UI served through NGINX.

### Build (DockerHub)

Build and run locally:
```bash
    # Build the Docker container
    docker build -t <your-dockerhub-username>/react-frontend:1.0.0 .
    
    # Push to DockerHub
    docker push <your-dockerhub-username>/react-frontend:1.0.0
```

Now visit 
```bash
  http://localhost:5173
```
to see the UI served through NGINX.

---

## API Integration

The frontend consumes the following backend REST APIs:

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/candidates` | Fetch list of all candidates |
| POST | `/api/vote` | Submit a new vote |
| PUT | `/api/vote` | Update existing vote |
| DELETE | `/api/vote/{email}` | Delete vote by email |
| GET | `/api/results` | Fetch current vote counts |
| GET | `/api/votes/{email}` | Check if user already voted |

All network logic is encapsulated in ```src/lib/api.js```.

---

🧮 Project Structure

```
react-app/
├── src/
│   ├── components/
│   │   ├── CandidateForm.jsx     # Main voting form logic
│   │   └── VoteResults.jsx       # Results list UI
│   ├── lib/
│   │   └── api.js                # REST API abstraction
│   ├── App.jsx                   # Root component
│   ├── main.jsx                  # Entry point
│   ├── index.css                 # Global styles
│   └── App.css                   # Component-specific styles
├── nginx.conf                    # NGINX proxy config (used in container)
├── Dockerfile                    # Multi-stage Docker build
├── vite.config.js                # Vite dev/prod configuration
├── package.json                  # Dependencies and scripts
└── README.md                     # (this file)
```

---

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `VITE_API_BASE_URL` | Optional override for backend API base URL | `''` (same origin / proxy) |
| `BACKEND` | Used by Vite proxy in dev mode | `http://localhost:8080` |

---

## UI Components

- **CandidateForm.jsx** — handles:
  - email validation and vote submission 
  - edit/delete existing votes 
  - mode toggle between select and type 
- **VoteResults.jsx** — handles:
  - fetching current results 
  - highlighting current leader(s)
  - live refresh button

---

## Developer Notes

- The SPA (single-page app) is built and served statically.

- All API calls go through ```/api/*``` — proxied either by Vite (dev) or NGINX (prod).

- Candidate list refreshes automatically after vote updates/deletions.

- In production, NGINX proxies API traffic to ```spring-service:8080``` (internal K8s DNS).

---

## License

MIT © 2025 Trinity College Voting System Team.
For internal academic and demonstration use only.

---