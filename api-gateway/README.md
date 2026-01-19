# API Gateway

## Overview
The API Gateway serves as the single entry point for the Intelligent Personal Trainer microservices architecture. It routes incoming requests from the frontend or external clients to the appropriate backend services.

## Key Features

*   **Centralized Routing:** Powered by Spring Cloud Gateway, it dynamically routes traffic based on path predicates.
    *   `/users/**` -> **User Service**
    *   `/trainer/**` -> **Trainer Service**
    *   `/data-processor/**` -> **Data Processor Service**
    *   `/data-persistence/**` -> **Data Persistence Service**
*   **CORS Configuration:** Configured to support Cross-Origin Resource Sharing, allowing the frontend application (running on localhost) to communicate securely with backend services.
*   **Protocol:** Handles HTTP traffic forwarding.
