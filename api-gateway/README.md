# API Gateway

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.0-brightgreen?style=flat-square&logo=spring)
![Spring Cloud Gateway](https://img.shields.io/badge/Spring_Cloud_Gateway-blue?style=flat-square&logo=spring)

## Overview

The `api-gateway` acts as the single entry point for all frontend requests to the Intelligent Personal Trainer backend. It is built on Spring Cloud Gateway, providing a reactive, non-blocking API routing mechanism.

## Key Features

*   **Dynamic Routing:** Uses the `lb://` scheme to integrate with Eureka (Service Discovery) and load balance requests to backend microservices automatically without hardcoded URLs.
*   **CORS Centralization:** Manages Cross-Origin Resource Sharing (CORS) globally, currently configured to allow requests from the local Angular frontend (`http://localhost:4200`).
*   **OpenAPI Aggregation:** Aggregates Swagger/OpenAPI documentation from all underlying microservices into a single, unified Swagger UI interface accessible at the gateway level.

## Prerequisites

*   Java 21
*   Maven 3.9+
*   Service Discovery (Eureka) must be running.

## Running the Service

### Local Development (Maven)

```bash
mvn spring-boot:run
```

### Docker Compose

```bash
cd ..
docker compose up -d api-gateway
```

## Endpoints

The Gateway exposes standard routes mapping to the microservices:
*   `/users/**` -> `user-service`
*   `/trainer/**` -> `trainer-service`
*   `/data-persistence/**` -> `data-persistence-service`
*   `/data-processor/**` -> `data-processor-service`

**Aggregated Swagger UI:** `http://localhost:9000/swagger-ui.html`
