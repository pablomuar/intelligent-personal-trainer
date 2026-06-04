# Data Persistence Service

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.0-brightgreen?style=flat-square&logo=spring)
![TimescaleDB](https://img.shields.io/badge/TimescaleDB-15-yellow?style=flat-square&logo=timescaledb)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-7.3.0-black?style=flat-square&logo=apachekafka)

## Overview

The `data-persistence-service` acts as the long-term memory for the user's fitness telemetry. It is designed to efficiently store and query time-series data, specifically focusing on the daily biometric aggregates and workout logs.

## Key Features

*   **Kafka Consumer:** Subscribes to the `FITNESS_DATA_TOPIC` to asynchronously ingest normalized `FitnessData` events published by the `data-processor-service`.
*   **Time-Series Storage:** Utilizes TimescaleDB (an extension of PostgreSQL) to efficiently store chronological data. It leverages PostgreSQL's native `jsonb` type to handle heterogeneous lists of exercises or varied workout structures flexibly.
*   **MCP Server (`getFitnessData`):** Exposes a Model Context Protocol (MCP) tool. This allows the cognitive AI (`trainer-service`) to synchronously request a user's recent or historical fitness data, step counts, and fatigue markers to tailor its advice accurately.
*   **Historical API:** Provides a REST endpoint to query fitness history based on specific date ranges (`from` and `to`).

## Prerequisites

*   Java 21
*   Maven 3.9+
*   TimescaleDB / PostgreSQL (running via Docker Compose or locally)
*   Apache Kafka (running via Docker Compose or locally)

## Environment Variables

| Variable | Description | Default / Local |
| :--- | :--- | :--- |
| `POSTGRES_USER` | Database username. | `postgres` |
| `POSTGRES_PASSWORD` | Database password. | - |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS`| Kafka broker address. | `localhost:9092` |

## Running the Service

### Local Development (Maven)

Ensure your infrastructure (Eureka, TimescaleDB, Kafka) is running.

```bash
export POSTGRES_USER=postgres
export POSTGRES_PASSWORD=your_password

mvn spring-boot:run
```

### Docker Compose

```bash
cd ..
docker compose up -d data-persistence-service
```

## Endpoints

*   `GET /data-persistence/fitness-data/{userId}`: Retrieve fitness history. Accepts `from` and `to` query parameters in `YYYY-MM-DD` format.
*   `GET /sse`: Server-Sent Events endpoint for MCP communication.
