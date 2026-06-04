# Trainer Service (Cognitive AI Core)

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.0-brightgreen?style=flat-square&logo=spring)
![Spring AI](https://img.shields.io/badge/Spring_AI-1.0.0--M1-green?style=flat-square&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-pgvector-blue?style=flat-square&logo=postgresql)
![Gemini](https://img.shields.io/badge/Gemini-3_Flash-blue?style=flat-square&logo=google)

## Overview

The `trainer-service` is the cognitive brain of the Intelligent Personal Trainer platform. It implements an autonomous Agentic AI using the ReAct pattern, Spring AI, and the Model Context Protocol (MCP). It is responsible for orchestrating conversations, generating personalized fitness plans, and ensuring all recommendations are medically safe.

## Key Features

*   **Agentic AI (MCP Client):** Acts as an MCP Client to dynamically discover and invoke tools. It connects to internal microservices (`user-service`, `data-persistence-service`) and external APIs (like Google Maps) to gather context before answering user queries.
*   **Clinical RAG System:** Integrates a Retrieval-Augmented Generation (RAG) system using `pgvector` in PostgreSQL. It searches a vector database of clinical guidelines to ground the AI's responses in medical evidence, minimizing hallucinations, especially when dealing with chronic pathologies.
*   **LLM Integration:** Powered by Google's Gemini models (`gemini-3-flash-preview`) for natural language understanding and generation, configured with specific system prompts for a professional, clinical tone.
*   **Conversation Memory:** Maintains conversational state and history using a local PostgreSQL database (separate from the time-series data).

## Prerequisites

*   Java 21
*   Maven 3.9+
*   PostgreSQL with `pgvector` extension (running via Docker Compose or locally)
*   Google Gemini API Key
*   Google Maps API Key (for external MCP tool integration)

## Environment Variables

The service relies on the following key environment variables:

| Variable | Description |
| :--- | :--- |
| `POSTGRES_USER` | Database username. |
| `POSTGRES_PASSWORD` | Database password. |
| `GEMINI_API_KEY` | Your Google Gemini API Key for LLM and Embeddings. |
| `GOOGLE_MAPS_API_KEY` | API key for the Google Maps MCP integration. |

*Note: In `application.properties`, these map to `spring.datasource.username`, `spring.ai.google.genai.api-key`, etc.*

## Running the Service

### Local Development (Maven)

Ensure your infrastructure (Eureka, PostgreSQL) is running.

```bash
export POSTGRES_USER=postgres
export POSTGRES_PASSWORD=your_password
export GEMINI_API_KEY=your_gemini_api_key
export GOOGLE_MAPS_API_KEY=your_google_maps_api_key

mvn spring-boot:run
```

### Docker Compose

This service is part of the global `docker-compose.yml` infrastructure. To run it alongside its dependencies:

```bash
cd ..
docker compose up -d trainer-service
```

## Endpoints

This service primarily exposes internal REST endpoints consumed by the API Gateway and frontend for chat interactions, and integrates via MCP to other services.

*   `POST /trainer/chat`: Main endpoint for Agentic Chat interaction.
*   `GET /trainer/chat/conversations`: Retrieves conversation history.
*   `DELETE /trainer/chat/conversations/{id}`: Deletes a specific conversation.
