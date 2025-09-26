#!/bin/bash

# Intelligent Personal Trainer - Microservices Stop Script
echo "Stopping Intelligent Personal Trainer Microservices..."

# Function to stop a service
stop_service() {
    local service_name=$1
    if [ -f "logs/$service_name.pid" ]; then
        local pid=$(cat logs/$service_name.pid)
        echo "Stopping $service_name (PID: $pid)..."
        kill $pid 2>/dev/null || echo "$service_name was not running"
        rm -f logs/$service_name.pid
    else
        echo "$service_name PID file not found"
    fi
}

# Stop services
stop_service "api-gateway"
stop_service "user-service"
stop_service "workout-service"
stop_service "progress-service"

echo "All services stopped."
echo "Logs are preserved in the 'logs' directory."