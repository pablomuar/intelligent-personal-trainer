# Trainer Web

![Angular](https://img.shields.io/badge/Angular-18.1.0-red?style=flat-square&logo=angular)
![TypeScript](https://img.shields.io/badge/TypeScript-5.9-blue?style=flat-square&logo=typescript)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-3.4-38B2AC?style=flat-square&logo=tailwind-css)
![Nginx](https://img.shields.io/badge/Nginx-latest-green?style=flat-square&logo=nginx)

## Overview

`trainer-web` is the presentation layer for the Intelligent Personal Trainer platform. It is a Single Page Application (SPA) built with Angular and styled with Tailwind CSS. It provides the user interface for interacting with the Agentic AI and viewing fitness history.

## Key Features

*   **Agentic Chat UI:** A reactive chat interface where users converse with the AI Personal Trainer. It handles streaming responses, displays parsed workout plans clearly, and manages conversation history.
*   **Fitness History Dashboard:** Displays historical biometric data and past workouts retrieved from the `data-persistence-service`.
*   **Manual Data Entry:** Provides forms for users to manually input their physical profiles and log workouts if not using automatic sync.
*   **Containerized Serving:** The application is built into static files and served efficiently using an Nginx web server within a Docker container.

## Prerequisites

*   Node.js (18+ recommended)
*   npm
*   Angular CLI (`npm i -g @angular/cli`)

## Environment Setup

The application communicates with the backend via the `api-gateway`. By default, it expects the gateway to be running at `http://localhost:9000`. This is typically configured in the Angular environment files (`src/environments/`).

## Running the Application

### Local Development

1.  Navigate to the directory:
    ```bash
    cd trainer-web
    ```
2.  Install dependencies:
    ```bash
    npm install
    ```
3.  Start the development server:
    ```bash
    npx ng serve
    ```
    The application will be available at `http://localhost:4200`.

### Running Tests

To run unit tests locally (especially useful if running in a headless environment without an X server):
```bash
npx ng test --watch=false --browsers=ChromeHeadless
```

### Docker Compose

This service is part of the global `docker-compose.yml` infrastructure, where it is built and served via Nginx.

```bash
cd ..
docker compose up -d trainer-web
```
Access the application at `http://localhost:4200` (port mapped from the Nginx container).
