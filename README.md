# Trinity College Voting Application — Full Stack on Kubernetes

A two-service, containerized React + Spring Boot application deployed on Kubernetes (Docker Desktop). The React frontend talks to the Spring microservice via in-cluster service discovery.

## Repository Layout

```
trinity-voting-app/
├── README.md                 # (this file) end-to-end instructions
├── spring-app/               # Spring Boot microservice (REST CRUD + Swagger UI)
│   └── README.md             # backend-only developer guide
├── react-app/                # React frontend microservice (Vite + NGINX)
│   └── README.md             # frontend-only developer guide
└── kubernetes/               # K8s manifests
├── spring-deployment.yaml
├── spring-service.yaml
├── react-deployment.yaml
└── react-service.yaml
```

---

## Prerequisites

- **Docker Desktop** with Kubernetes enabled
- **kubectl** in your PATH
- **DockerHub account** (for `docker push`)
- **Node.js 20+** (only if you want to run/build locally)
- **Java 21 + Maven** (only if you want to run backend locally)

> **Tip:** Make sure Docker Desktop shows "Kubernetes: Running".

---

## Quick Run

```bash
    # -1) setup keyboard shortcut
    alias k=kubectl
    
    # 0) point k8s at docker desktop's cluster
    k config get-contexts
    kubectl config use-context docker-desktop 
    k cluster-info
    
    # Step 1) deploy spring boot and react
    kubectl apply -f kubernetes/spring-deployment.yaml
    kubectl apply -f kubernetes/react-deployment.yaml
    kubectl apply -f kubernetes/react-service.yaml   
    kubectl apply -f kubernetes/spring-service.yaml
    
    # Step 2) verify deployments and services are running
    kubectl get deployments      
    kubectl get services       
    
    # Step 3) Open the app at 
    http://localhost:5173
    
    # Step 4) Clean up
    k delete -f kubernetes/react-service.yaml
    k delete -f kubernetes/react-deployment.yaml
    k delete -f kubernetes/spring-service.yaml
    k delete -f kubernetes/spring-deployment.yaml

```

## What You'll Deploy

### Spring microservice (2 replicas)
- REST endpoints under `/api/**`
- Swagger UI at `/swagger-ui`
- In-cluster DNS name: `spring-service:8080`

### React frontend (2 replicas)
- Built with Vite; served by NGINX
- Calls backend via `http://spring-service:8080/api/...`
- Exposed via `react-service` (NodePort or port-forward)

## Build & Push Images

You'll build two images: one for Spring, one for React.

### 1) Spring image (backend)

From `spring-app/`:
```bash
    # build a container image via Spring Boot plugin
    ./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=<your-dockerhub-username>/trinity-spring:1.0.0
    
    # push to DockerHub
    docker push <your-dockerhub-username>/trinity-spring:1.0.0
```
> If you used a different image name previously, update ```spring-deployment.yaml``` accordingly.
 

2) React image (frontend)
From ```react-app/```:
```bash
    # production build
    npm ci
    npm run build


    # build NGINX image
    docker build -t <your-dockerhub-username>/trinity-react:1.0.0 .
    
    # push
    docker push <your-dockerhub-username>/trinity-react:1.0.0
```

---

## Configure Kubernetes Manifests (quick check)
Open files in ```kubernetes/``` and confirm the image fields match what you pushed:

- ```spring-deployment.yaml``` → ```image: <your-dockerhub-username>/trinity-spring:1.0.0```
- ```react-deployment.yaml``` → ```image: <your-dockerhub-username>/trinity-react:1.0.0```

---

## Deploy to Kubernetes

From the repo root (or from kubernetes/):
```bash
    # Spring microservice
    kubectl apply -f kubernetes/spring-deployment.yaml
    kubectl apply -f kubernetes/spring-service.yaml
    
    # React frontend
    kubectl apply -f kubernetes/react-deployment.yaml
    kubectl apply -f kubernetes/react-service.yaml
```

Verify:
```bash
    kubectl get all
```

You should see:

- 2 Deployments (spring, react)
- 2 ReplicaSets
- 4 Pods total (2 spring + 2 react)
- 2 Services (spring-service, react-service)

---

## Access the App (two options)

**Option A — NodePort** (preferred if react-service is NodePort)
```bash
  kubectl get service react-service
```

Look for a ```NODE-PORT``` (e.g., ```30080```). Open:
```bash
  http://localhost:<NODE-PORT>
```

**Option B — Port-forward** (works with ClusterIP too)
```bash
kubectl port-forward svc/react-service 5173:80
```

Open:
```bash
http://localhost:5173
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

## Cleanup

```bash
kubectl delete -f kubernetes/react-service.yaml
kubectl delete -f kubernetes/react-deployment.yaml
kubectl delete -f kubernetes/spring-service.yaml
kubectl delete -f kubernetes/spring-deployment.yaml
```

---

## Troubleshooting
- **React shows network errors**

Ensure React is calling ```http://spring-service:8080/api/...``` (handled by your NGINX config).
Check service: ```kubectl get svc spring-service```.

- **Pods CrashLoopBackOff**

```kubectl logs <pod-name>``` and ```kubectl describe pod <pod-name>```.

- Can't reach UI

If ```react-service``` is ```ClusterIP```, use **port-forward** (above) or change it to **NodePort**.

- Verify DNS

In a debug pod: ```curl http://spring-service:8080/actuator/health``` (if actuator is present) or one of your ```/api/*``` endpoints.

---

## Location for Screenshots for Submission

```bash
    screenshots/
    ├── kubectl-get-all.png         # shows 2 Deployments, 2 RS, 4 Pods, 2 Services
    └── working-ui.png              # React UI with visible URL and successful CRUD
```