FROM openjdk:17-jdk-alpine

WORKDIR /app

# Copy the Maven project files
COPY pom.xml ./
COPY src ./src

# Install Maven
RUN apk add --no-cache maven

# Build the application
RUN mvn clean package -DskipTests

# Expose the port
EXPOSE 8082

# Run the application
CMD ["java", "-jar", "target/workout-service-1.0.0.jar"]