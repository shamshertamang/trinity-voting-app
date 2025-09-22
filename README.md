# Trinity College Voting Application

A simple web-based voting application built with Spring Boot that allows Trinity College students and staff to vote for candidates using their institutional email addresses.

## Features

- **Email Validation**: Only accepts votes from valid `@trincoll.edu` email addresses with strict format validation
- **One Vote Per Person**: Prevents duplicate voting by tracking email addresses
- **Dynamic Candidate Management**: Users can vote for existing candidates or add new ones
- **Real-time Updates**: New candidates added by voters become available to subsequent voters
- **Simple Interface**: Clean, user-friendly web interface for easy voting

## How It Works

1. **Voter Authentication**: Users must enter a valid Trinity College email address
2. **Voting Options**:
    - Select from existing candidates in a dropdown menu
    - Type in a new candidate name to add them to the system
3. **Vote Processing**: Each vote is recorded and candidate vote counts are updated
4. **Candidate Pool**: New candidates are automatically added to the selection list for future voters

## Technical Stack

- **Backend**: Spring Boot 3.2.0
- **Database**: H2 In-Memory Database
- **Frontend**: Vanilla HTML, CSS, and JavaScript
- **Build Tool**: Maven
- **Java Version**: 17
- **Containerization**: Docker

## Quick Start with Docker (Recommended)

The easiest way to run this application is with Docker - no need to install Java or Maven!

### Prerequisites
- Docker
- Git

### Run the Application
```bash
# Clone the repository
git clone https://github.com/shamshertamang/my-spring-container.git
cd my-spring-container

# Build and run with Docker
docker build -t voting-app .
docker run -p 8080:8080 voting-app
```

### Access the Application
Open your browser and navigate to: `http://localhost:8080`


## Prerequisites (for developers)

- Java 21
- Maven 3.6+ (or use the included Maven wrapper)
- Git

## Manual Setup Instructions (for developers)

### 1. Clone the Repository

```bash
git clone https://github.com/shamshertamang/my-spring-container.git
cd myspringcontainer
```

### 2. Build the Project

Using Maven wrapper (recommended):
```bash
./mvnw clean compile
```

Or using system Maven:
```bash
mvn clean compile
```

### 3. Run the Application

Using Maven wrapper:
```bash
./mvnw spring-boot:run
```

Or using system Maven:
```bash
mvn spring-boot:run
```

### 4. Access the Application

Open your web browser and navigate to:
```
http://localhost:8080
```

## Docker Support

### Build the Docker Image
```bash
docker build -t voting-app .
```

### Run the Container
```bash
docker run -p 8080:8080 voting-app
```

### Run in Background (Detached Mode)
```bash
docker run -d -p 8080:8080 --name voting-container voting-app
```

### Container Management
```bash
# Stop the container
docker stop voting-container

# Start the stopped container
docker start voting-container

# View container logs
docker logs voting-container

# View logs in real-time
docker logs -f voting-container

# Remove the container
docker rm voting-container

# Remove the Docker image
docker rmi voting-app
```

### Access the Dockerized Application
Open your browser and navigate to: `http://localhost:8080`


## Usage

1. **Enter your Trinity College email** in the format: `yourname@trincoll.edu`
2. **Choose your voting method**:
    - Select an existing candidate from the dropdown menu, OR
    - Enter a new candidate name in the text field
3. **Submit your vote**
4. **Confirmation**: You'll see a success message confirming your vote was recorded

## Email Validation Rules

The application enforces strict email validation:
- Must end with `@trincoll.edu`
- Must have exactly one dot (`.`) in the entire email address
- No additional dots are allowed before the `@` symbol

**Valid Examples:**
- `jsmith@trincoll.edu` ✅
- `mary@trincoll.edu` ✅

**Invalid Examples:**
- `j.smith@trincoll.edu` ❌ (extra dot)
- `john@gmail.com` ❌ (wrong domain)
- `student@trincoll.edu.gov` ❌ (extra domain)

## Project Structure

```
src/
├── main/
│   ├── java/com/javashams/springcontainer/
│   │   ├── SpringContainerApplication.java     # Main application class
│   │   ├── controller/VoteController.java      # REST API endpoints
│   │   ├── service/VotingService.java          # Business logic
│   │   ├── model/                              # Data models
│   │   │   ├── Vote.java
│   │   │   └── Candidate.java
│   │   └── repository/                         # Database repositories
│   │       ├── VoteRepository.java
│   │       └── CandidateRepository.java
│   └── resources/
│       ├── static/index.html                   # Frontend interface
│       └── application.properties              # Application configuration
```

## API Endpoints

- `GET /api/candidates` - Retrieve all candidates and their vote counts
- `POST /api/vote` - Submit a new vote

## Development Notes

- The application uses an H2 in-memory database, so all data is reset when the application restarts
- For production use, consider switching to a persistent database like PostgreSQL or MySQL
- The application runs on port 8080 by default
- **Docker**: The application can be containerised using the Dockerfile
- **Java Version**: Build and tested with JDK21

## License

This project is created for educational purposes at Trinity College.