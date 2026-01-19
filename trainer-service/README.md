# Trainer Service

## Overview
The Trainer Service is the "brain" of the platform. It leverages Generative AI to act as an intelligent personal trainer, offering interactive chat, workout advice, and personalized training plan generation.

## Key Features

*   **Generative AI Integration:**
    *   Powered by **Google Gemini** (via Spring AI) for high-quality natural language understanding and generation.
*   **Agentic Capabilities (MCP Client):**
    *   Acts as an AI Agent that can autonomously "use tools" to gather context.
    *   Connects to the **User Service** to read user profiles (age, injuries, goals).
    *   Connects to the **Data Persistence Service** to analyze past workout performance.
*   **RAG (Retrieval-Augmented Generation):**
    *   Uses **PGVector** to store and retrieve relevant fitness documentation. This ensures the AI's advice is grounded in verified fitness knowledge rather than just model training data.
*   **Features:**
    *   **Agentic Chat:** A stateful chat interface where the AI remembers the conversation context and uses its tools to answer complex questions about the user's progress.
    *   **Workout Plan Generator:** automated generation of detailed weekly workout plans tailored to the user's specific profile and history.
