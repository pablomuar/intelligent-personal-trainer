# User Service

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.0-brightgreen?style=flat-square&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat-square&logo=postgresql)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-7.3.0-black?style=flat-square&logo=apachekafka)

## Overview

The `user-service` is responsible for managing user identities, static physical profiles (weight, height, age), and clinical contexts (chronic pathologies, injuries). It acts as a foundational service for personalization within the Intelligent Personal Trainer platform.

## Key Features

*   **Profile Management:** Stores and retrieves user profiles, including crucial health conditions that dictate the safety parameters of workout plans.
*   **MCP Server (`getUserProfile`):** Exposes an MCP (Model Context Protocol) Server. This allows the Agentic AI in the `trainer-service` to dynamically invoke the `getUserProfile` tool to understand the user's biological and clinical context before giving advice.
*   **Scheduled Data Sync:** Runs a scheduled background task (`scheduler.daily-ingestion.cron`) to trigger the ingestion of daily fitness data via the `data-processor-service`.
*   **Error Handling (Kafka):** Consumes messages from a Kafka error topic (`FITNESS_DATA_ERROR_TOPIC`) to handle data processing failures, implementing retry logic by adjusting the user's `last_sync_date`.

## Prerequisites

*   Java 21
*   Maven 3.9+
*   PostgreSQL (running via Docker Compose or locally)
*   Apache Kafka (running via Docker Compose or locally)

## Environment Variables

The service relies on the following key environment variables:

| Variable | Description | Default / Local |
| :--- | :--- | :--- |
| `POSTGRES_USER` | Database username. | `postgres` |
| `POSTGRES_PASSWORD` | Database password. | - |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS`| Kafka broker address. | `localhost:9092` |

## Running the Service

### Local Development (Maven)

Ensure your infrastructure (Eureka, PostgreSQL, Kafka) is running.

```bash
export POSTGRES_USER=postgres
export POSTGRES_PASSWORD=your_password

mvn spring-boot:run
```

### Docker Compose

This service is part of the global `docker-compose.yml` infrastructure.

```bash
cd ..
docker compose up -d user-service
```

## Endpoints

*   `GET /user-service/users/{id}`: Retrieve a user's profile.
*   `POST /user-service/users`: Create a new user profile.
*   `PUT /user-service/users/{id}`: Update an existing user profile.
*   `GET /sse`: Server-Sent Events endpoint for MCP communication.
