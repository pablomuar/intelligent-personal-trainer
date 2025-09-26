# Intelligent Personal Trainer - Microservices Application

A comprehensive microservices-based intelligent personal trainer application built with Java Spring Boot. This application helps users manage their fitness journey through personalized workout plans, progress tracking, and analytics.

## Architecture

This project follows a microservices architecture with the following components:

### Services

1. **API Gateway** (Port 8080)
   - Routes requests to appropriate microservices
   - Single entry point for all client requests
   - Built with Spring Cloud Gateway

2. **User Service** (Port 8081)
   - User registration and authentication
   - User profile management
   - Fitness goals and experience level tracking

3. **Workout Service** (Port 8082)
   - Exercise library management
   - Workout plan creation and management
   - Exercise filtering by muscle group, equipment, and difficulty

4. **Progress Service** (Port 8083)
   - Workout progress tracking
   - Analytics and statistics
   - Historical data management

## Technology Stack

- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Cloud Gateway**
- **Spring Data JPA**
- **Spring Security**
- **H2 Database** (In-memory for development)
- **Maven** (Build tool)
- **Docker** (Containerization)

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (optional)

### Running Locally

1. **Clone the repository**
   ```bash
   git clone https://github.com/pablomuar/intelligent-personal-trainer.git
   cd intelligent-personal-trainer
   ```

2. **Build all services**
   ```bash
   mvn clean compile
   ```

3. **Run services individually**
   
   In separate terminal windows:
   
   ```bash
   # User Service
   cd user-service
   mvn spring-boot:run
   
   # Workout Service  
   cd workout-service
   mvn spring-boot:run
   
   # Progress Service
   cd progress-service
   mvn spring-boot:run
   
   # API Gateway
   cd api-gateway
   mvn spring-boot:run
   ```

### Running with Docker

1. **Build and run all services**
   ```bash
   docker-compose up --build
   ```

2. **Run in detached mode**
   ```bash
   docker-compose up -d
   ```

3. **Stop services**
   ```bash
   docker-compose down
   ```

## API Endpoints

### API Gateway (http://localhost:8080)

The API Gateway routes requests to the appropriate services:

- `/api/users/**` → User Service
- `/api/workouts/**` → Workout Service  
- `/api/progress/**` → Progress Service

### User Service Endpoints

- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `GET /api/users/health` - Health check

### Workout Service Endpoints

**Exercises:**
- `GET /api/workouts/exercises` - Get all exercises
- `GET /api/workouts/exercises/{id}` - Get exercise by ID
- `GET /api/workouts/exercises/muscle-group/{muscleGroup}` - Get exercises by muscle group
- `POST /api/workouts/exercises` - Create new exercise

**Workout Plans:**
- `GET /api/workouts/plans` - Get all workout plans
- `GET /api/workouts/plans/{id}` - Get workout plan by ID
- `GET /api/workouts/plans/user/{userId}` - Get workout plans by user
- `POST /api/workouts/plans` - Create new workout plan
- `PUT /api/workouts/plans/{id}` - Update workout plan
- `DELETE /api/workouts/plans/{id}` - Delete workout plan
- `GET /api/workouts/health` - Health check

### Progress Service Endpoints

- `GET /api/progress` - Get all progress records
- `GET /api/progress/{id}` - Get progress record by ID
- `GET /api/progress/user/{userId}` - Get progress records by user
- `GET /api/progress/user/{userId}/workout/{workoutPlanId}` - Get progress by user and workout
- `GET /api/progress/user/{userId}/analytics` - Get user analytics
- `POST /api/progress` - Create new progress record
- `PUT /api/progress/{id}` - Update progress record
- `DELETE /api/progress/{id}` - Delete progress record
- `GET /api/progress/health` - Health check

## Data Models

### User Entity
- ID, Username, Email, Password
- First Name, Last Name, Age
- Fitness Goal, Experience Level
- Created/Updated timestamps

### Exercise Entity
- ID, Name, Description
- Muscle Group, Equipment
- Difficulty Level (1-5), Instructions

### Workout Plan Entity
- ID, Name, Description, User ID
- Duration (weeks), Target Muscle Groups
- Fitness Goal, Difficulty Level
- Associated Workout Sessions

### Progress Record Entity
- ID, User ID, Workout Plan ID, Exercise ID
- Sets/Reps Completed, Weight Used
- Duration, Calories Burned, Notes
- Completion timestamps

## Database Access

Each service uses H2 in-memory database for development. Access the H2 consoles:

- User Service: http://localhost:8081/h2-console
- Workout Service: http://localhost:8082/h2-console
- Progress Service: http://localhost:8083/h2-console

**Connection Details:**
- JDBC URL: `jdbc:h2:mem:[servicename]db`
- Username: `sa`
- Password: `password`

## Health Checks

Each service provides health endpoints:
- API Gateway: http://localhost:8080/actuator/health
- User Service: http://localhost:8081/actuator/health
- Workout Service: http://localhost:8082/actuator/health
- Progress Service: http://localhost:8083/actuator/health

## Development

### Project Structure
```
intelligent-personal-trainer/
├── api-gateway/           # API Gateway service
├── user-service/          # User management service
├── workout-service/       # Workout and exercise service
├── progress-service/      # Progress tracking service
├── docker-compose.yml     # Docker composition
├── pom.xml               # Parent POM
└── README.md
```

### Building Individual Services
```bash
cd [service-name]
mvn clean package
```

### Running Tests
```bash
mvn test
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.