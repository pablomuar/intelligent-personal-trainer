# API Gateway

## Overview
The API Gateway serves as the single entry point for the Intelligent Personal Trainer microservices architecture. It routes incoming requests from the frontend or external clients to the appropriate backend services.

## Key Features

*   **Service Discovery Integration:** Integrated with **Netflix Eureka** to dynamically discover service locations.
*   **Centralized Routing:** Powered by **Spring Cloud Gateway**, it routes traffic based on path predicates using load-balanced service IDs (`lb://`):
    *   `/users/**` -> `lb://user-service`
    *   `/trainer/**` -> `lb://trainer-service`
    *   `/data-processor/**` -> `lb://data-processor-service`
    *   `/data-persistence/**` -> `lb://data-persistence-service`
*   **CORS Configuration:** Configured to support Cross-Origin Resource Sharing, allowing the frontend application (running on localhost) to communicate securely with backend services.
*   **Port:** Runs on port `9000` by default.
