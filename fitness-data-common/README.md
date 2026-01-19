# Fitness Data Common

## Overview
This module contains the shared data models, DTOs (Data Transfer Objects), and constants used across the Intelligent Personal Trainer ecosystem, specifically related to fitness activities.

## Key Features

*   **Fitness Data Model:** Defines the canonical structure for fitness records (`FitnessData`), including:
    *   Activity Type
    *   Duration and Intensity
    *   Calories Burned
    *   Timestamps
    *   Source Metadata
*   **Workout Data:** Defines structures for workout plans and exercises (`WorkoutData`).
*   **Constants:** Centralized definition of Kafka topic names and other shared constants to ensure consistency between Producers (Data Processor) and Consumers (Data Persistence).
