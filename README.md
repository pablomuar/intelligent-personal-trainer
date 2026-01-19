# Intelligent Personal Trainer

## Overview

The **Intelligent Personal Trainer** is a comprehensive microservices-based platform designed to provide personalized fitness guidance using Generative AI. It combines real-time data ingestion, long-term historical analysis, and an "Agentic" AI that understands the user's context (health, history, goals) to provide tailored workout plans and advice.

## Architecture

The system is composed of several specialized microservices, all accessible through a central API Gateway.

| Service | Description | Link |
| :--- | :--- | :--- |
| **API Gateway** | Central entry point and router for the system. | [README](api-gateway/README.md) |
| **Trainer Web** | Angular-based frontend dashboard and user interface. | [README](trainer-web/README.md) |
| **Trainer Service** | The AI core. Uses Gemini and RAG to generate plans and chat. | [README](trainer-service/README.md) |
| **User Service** | Manages user identities, authentication, and physical profiles. | [README](user-service/README.md) |
| **Data Processor** | Ingests and normalizes fitness data from various sources (CSV/API). | [README](data-processor-service/README.md) |
| **Data Persistence** | Stores historical fitness data in TimescaleDB and exposes it via API. | [README](data-persistence-service/README.md) |
| **Common Libs** | Shared data models and utilities. | [Fitness Data](fitness-data-common/README.md), [User Common](user-common/README.md) |

## Key Concepts

*   **Agentic AI:** The Trainer Service acts as an autonomous agent using the Model Context Protocol (MCP) to "call" other services (like User and Persistence) to gather information before answering user queries.
*   **Event-Driven:** Data flows asynchronously from ingestion (Processor) to storage (Persistence) via Kafka.
*   **RAG (Retrieval-Augmented Generation):** The AI has access to a vector database of fitness knowledge to ensure its advice is scientifically sound.
