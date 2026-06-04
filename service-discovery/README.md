# Service Discovery

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.0-brightgreen?style=flat-square&logo=spring)
![Netflix Eureka](https://img.shields.io/badge/Netflix_Eureka-blue?style=flat-square&logo=netflix)

## Overview

The `service-discovery` module is an essential infrastructure component based on Netflix Eureka Server. It acts as a registry where all other microservices announce their presence and network locations upon startup.

## Key Features

*   **Dynamic Registration:** Allows microservices to dynamically register their IP addresses and ports, eliminating the need for hardcoded configurations in the API Gateway or Feign clients.
*   **Client-Side Load Balancing:** Enables components like the Spring Cloud Gateway to discover available instances of a service and distribute traffic among them.

## Prerequisites

*   Java 21
*   Maven 3.9+

## Running the Service

This is typically the first service that should be started.

### Local Development (Maven)

```bash
mvn spring-boot:run
```

### Docker Compose

```bash
cd ..
docker compose up -d service-discovery
```

## Accessing the Registry

The Eureka Server dashboard can be accessed to view currently registered microservices:

*   **URL:** `http://localhost:8761`
