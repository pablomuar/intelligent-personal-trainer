# Data Processor Service

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.0-brightgreen?style=flat-square&logo=spring)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-7.3.0-black?style=flat-square&logo=apachekafka)

## Overview

The `data-processor-service` is a stateless, event-driven Extract, Transform, Load (ETL) microservice. Its primary responsibility is to ingest raw biometric and fitness data from various external sources (e.g., CSV dumps, external APIs like Samsung Health or datasets like PMData), normalize it into a canonical structure, and broadcast it to the rest of the system.

## Key Features

*   **Asynchronous Ingestion:** Designed to handle large files or slow external APIs without blocking. When a request is received, it immediately returns a response to the caller and offloads the processing to a background thread (`@Async`).
*   **Data Normalization:** Converts vendor-specific formats into a standardized `FitnessData` DTO, ensuring the downstream AI and storage services have a consistent view of the user's health.
*   **Kafka Producer:** Acts as the entry point for the event-driven architecture. Once data is processed, it publishes the `FitnessData` objects to the `FITNESS_DATA_TOPIC`.
*   **Error Handling:** In case of processing failures, it publishes error events to the `FITNESS_DATA_ERROR_TOPIC`, which the `user-service` consumes to manage retry logic.

## Prerequisites

*   Java 21
*   Maven 3.9+
*   Apache Kafka (running via Docker Compose or locally)

## Environment Variables

| Variable | Description | Default / Local |
| :--- | :--- | :--- |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS`| Kafka broker address. | `localhost:9092` |

## Running the Service

### Local Development (Maven)

Ensure your infrastructure (Eureka, Kafka) is running.

```bash
mvn spring-boot:run
```

*Note: In local mode without Docker, ensure `/app/data/sources.json` or the relevant paths configured in `application.properties` (`datasource.sources-path`) exist or are overridden.*

### Docker Compose

```bash
cd ..
docker compose up -d data-processor-service
```

## Endpoints

*   `POST /processor/process`: Triggers the ingestion process for a specific user ID. Returns HTTP 202 Accepted immediately while processing happens asynchronously.
