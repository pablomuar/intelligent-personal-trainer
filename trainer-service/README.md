# Trainer Service

## Overview
The Trainer Service is the "brain" of the platform. It leverages Generative AI to act as an intelligent personal trainer, offering interactive chat, workout advice, and personalized training plan generation.

## Key Features

*   **Generative AI Integration:**
    *   Powered by **Google Gemini** (via Spring AI) for high-quality natural language understanding and generation.
*   **Agentic Capabilities (MCP Client):**
    *   Acts as an AI Agent that can autonomously "use tools" to gather context.
    *   **TrainerChatService**: Orchestrates stateful chat sessions, managing persistence and history.
    *   **AgenticTrainerChatService**: Stateless service that interfaces with the LLM and executes tools.
    *   Connects to the **User Service** to retrieve user profiles (age, injuries, goals).
    *   Connects to the **Data Persistence Service** to analyze past workout performance.
*   **Integrated RAG (Retrieval-Augmented Generation):**
    *   Includes a built-in RAG module to ingest and retrieve relevant fitness documentation.
    *   Ensures the AI's advice is grounded in verified fitness knowledge rather than just model training data.
*   **Features:**
    *   **Agentic Chat:** The primary interface for all interactions. The AI remembers conversation context and uses its tools to answer complex questions about the user's progress.
    *   **Plan Generation:** Users can request detailed weekly workout plans directly through the chat interface. The AI generates structured plans tailored to the user's specific profile and history.
