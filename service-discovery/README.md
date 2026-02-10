# Service Discovery

## Overview
The Service Discovery module is a critical component of the microservices architecture, acting as the central registry for all backend services. It is built using **Netflix Eureka Server**.

## Key Features

*   **Service Registry:** allows services to register themselves dynamically at startup.
*   **Health Monitoring:** continuously checks the health of registered services.
*   **Load Balancing:** enables the API Gateway and other services to locate instances of backend services for load balancing and failover.

## Configuration

*   **Port:** Runs on port `8761` by default.
*   **Dashboard:** Provides a web dashboard at `http://localhost:8761` to view registered instances.

## Integration

All other microservices (`user-service`, `trainer-service`, `data-persistence-service`, `data-processor-service`, `api-gateway`) are configured as Eureka Clients and will automatically register with this server upon startup.
