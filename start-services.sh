#!/bin/bash

# Intelligent Personal Trainer - Microservices Startup Script
echo "Starting Intelligent Personal Trainer Microservices..."

# Function to start a service
start_service() {
    local service_name=$1
    local port=$2
    echo "Starting $service_name on port $port..."
    cd $service_name
    mvn spring-boot:run > ../logs/$service_name.log 2>&1 &
    echo $! > ../logs/$service_name.pid
    cd ..
}

# Create logs directory
mkdir -p logs

# Start services in order (dependencies first)
echo "Starting backend services..."
start_service "user-service" "8081"
sleep 5

start_service "workout-service" "8082"
sleep 5

start_service "progress-service" "8083"
sleep 5

echo "Starting API Gateway..."
start_service "api-gateway" "8080"

echo ""
echo "All services are starting up..."
echo "Check logs in the 'logs' directory for detailed output."
echo ""
echo "Service URLs:"
echo "- API Gateway: http://localhost:8080"
echo "- User Service: http://localhost:8081"
echo "- Workout Service: http://localhost:8082"
echo "- Progress Service: http://localhost:8083"
echo ""
echo "Health Checks:"
echo "- API Gateway: http://localhost:8080/actuator/health"
echo "- User Service: http://localhost:8081/actuator/health"
echo "- Workout Service: http://localhost:8082/actuator/health"
echo "- Progress Service: http://localhost:8083/actuator/health"
echo ""
echo "To stop all services, run: ./stop-services.sh"