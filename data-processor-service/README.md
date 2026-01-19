# Data Processor Service

## Overview
The Data Processor Service is responsible for the ingestion, normalization, and publication of fitness data. It acts as a bridge between external data sources and the internal event streaming architecture.

## Key Features

*   **Data Ingestion:**
    *   Accepts fitness data via REST API or from CSV files.
    *   Supports multiple data sources via a flexible `sourceId` mechanism.
*   **Data Normalization:**
    *   Uses an Adapter pattern (`FitnessDataReader`) to standardize incoming data from various formats into the canonical `FitnessData` structure.
*   **Event Publishing:**
    *   **Kafka Producer:** Publishes normalized fitness records to a Kafka topic. This decouples data ingestion from storage and analysis, allowing for asynchronous processing.
*   **Asynchronous Processing:** Configured to handle data loads efficiently without blocking the ingestion API.
