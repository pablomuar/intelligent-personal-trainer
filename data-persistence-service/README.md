# Data Persistence Service

## Overview
The Data Persistence Service manages the long-term storage and retrieval of user fitness history. It is designed to handle time-series data efficiently and provide access to this data for both the frontend and AI agents.

## Key Features

*   **Event-Driven Ingestion:**
    *   **Kafka Consumer:** Listens to the fitness data topic and automatically persists incoming records.
*   **Time-Series Storage:**
    *   Utilizes **TimescaleDB** (based on PostgreSQL) to optimize the storage and querying of time-stamped fitness activities.
*   **Data Access API:**
    *   Provides REST endpoints to retrieve historical fitness data for a specific user within a given date range.
*   **Service Discovery:** Automatically registers with the **Eureka Server** for dynamic discovery.
*   **MCP Tool Integration:**
    *   **`getFitnessData`:** Exposes a Model Context Protocol (MCP) tool. This allows the AI Trainer to programmatically query a user's workout history (filtering by date) to generate context-aware advice and plans.
