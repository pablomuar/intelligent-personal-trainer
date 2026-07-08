# Intelligent Personal Trainer

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.0-brightgreen?style=flat-square&logo=spring)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2025-blue?style=flat-square&logo=spring)
![Angular](https://img.shields.io/badge/Angular-18.1.0-red?style=flat-square&logo=angular)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat-square&logo=postgresql)
![TimescaleDB](https://img.shields.io/badge/TimescaleDB-latest-yellow?style=flat-square&logo=timescaledb)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-7.3.0-black?style=flat-square&logo=apachekafka)
![Gemini](https://img.shields.io/badge/Gemini-3_Flash-blue?style=flat-square&logo=google)

## Overview

The **Intelligent Personal Trainer** is a comprehensive microservices-based platform designed to provide personalized fitness guidance using Generative AI. It combines real-time data ingestion, long-term historical analysis, and an "Agentic" AI that understands the user's context (health, history, goals) to provide tailored workout plans and advice, adapting safely to chronic pathologies (such as diabetes or heart conditions).

## Global Data Flow Architecture

The system employs a dual-paradigm architecture to handle different operational needs:

1.  **Asynchronous Ingestion via Kafka (Event-Driven):**
    *   **Data Processor Service** receives large datasets (CSV, APIs) via HTTP. It immediately returns a response and asynchronously normalizes the data into standard `FitnessData` objects.
    *   These objects are published as events to an Apache Kafka topic.
    *   **Data Persistence Service** consumes these events and stores the time-series biometric telemetry in TimescaleDB.
    *   This flow ensures high throughput, scalability, and resilience during bulk data imports without blocking synchronous API calls.

2.  **Synchronous Orchestration via MCP (Model Context Protocol):**
    *   The **Trainer Service** acts as the cognitive core and *MCP Client*.
    *   When the Agentic AI needs information to formulate a response, it dynamically discovers and invokes tools exposed by other microservices acting as *MCP Servers*.
    *   For example, it synchronously requests the user's profile from the **User Service** or retrieves fitness history from the **Data Persistence Service** to contextualize its AI model prompts.

## Microservices Architecture

The system is composed of several specialized microservices, all accessible through a central API Gateway and coordinated via Service Discovery.

| Service | Description | Link |
| :--- | :--- | :--- |
| **Trainer Service** | The cognitive AI core using Gemini, a local clinical RAG system with pgvector, and acts as an MCP Client. | [README](trainer-service/README.md) |
| **User Service** | Manages identities, static health data, pathologies, and acts as an MCP Server (`getUserProfile`). | [README](user-service/README.md) |
| **Data Persistence** | Stores time-series biometric telemetry in TimescaleDB and acts as an MCP Server (`getFitnessData`). | [README](data-persistence-service/README.md) |
| **Data Processor** | Stateless ETL service that ingests, normalizes, and publishes fitness data to Kafka. | [README](data-processor-service/README.md) |
| **API Gateway** | Reactive entry point, dynamic routing, CORS policy centralization, and OpenAPI aggregator. | [README](api-gateway/README.md) |
| **Service Discovery** | Netflix Eureka server for dynamic service registration and load balancing. | [README](service-discovery/README.md) |
| **Trainer Web** | Angular SPA frontend featuring an Agentic Chat UI, served via Nginx. | [README](trainer-web/README.md) |
| **Common Libs** | Shared data models and utilities. | [Fitness Data](fitness-data-common/README.md), [User Common](user-common/README.md) |

## Quick Start (Docker Compose)

The entire infrastructure can be brought up using Docker Compose.

### Prerequisites

*   Docker and Docker Compose (V2)
*   Java 21 (for local builds)
*   Node.js & npm (for frontend local builds)

### Environment Variables

Before running the system, set the following environment variables (e.g., in a `.env` file or export them):

```bash
export POSTGRES_USER=postgres
export POSTGRES_PASSWORD=your_password
export DOCKER_IMAGE_PREFIX=your_dockerhub_username
export GEMINI_API_KEY=your_gemini_api_key
export GOOGLE_MAPS_API_KEY=your_google_maps_api_key
```

### Running the Infrastructure

1.  Build the images (if not pulling from a registry):
    *   *Note: Ensure you have built the Spring Boot and Angular apps into Docker images matching the `DOCKER_IMAGE_PREFIX` before running compose.*

2.  Start the services:
    ```bash
    docker compose up -d
    ```

3.  Access the applications:
    *   **Frontend (Trainer Web):** `http://localhost:4200`
    *   **API Gateway:** `http://localhost:9000`
    *   **Service Discovery (Eureka):** `http://localhost:8761`

To stop the services:
```bash
docker compose down
```
