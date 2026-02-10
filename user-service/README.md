# User Service

## Overview
The User Service handles all user-related operations, including registration, authentication, and profile management. It acts as the source of truth for user identities and physical attributes.

## Key Features

*   **User Management:**
    *   **Registration:** Endpoint to create new user accounts with detailed physical profiles.
    *   **Authentication:** Provides login capabilities, verifying credentials and returning user details.
    *   **Profile Updates:** Allows users to update their physical stats (weight, age, diseases, etc.).
*   **Data Storage:** Utilizes an H2 in-memory database for fast, lightweight user data persistence during operation.
*   **Service Discovery:** Automatically registers with the **Eureka Server** for dynamic discovery.
*   **MCP Tool Integration:**
    *   **`getUserProfile`:** Exposes a tool compliant with the Model Context Protocol (MCP). This allows AI agents (like the Trainer Service) to autonomously query and retrieve rich user profile data to personalize responses.
