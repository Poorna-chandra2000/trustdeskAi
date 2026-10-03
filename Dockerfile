# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first to leverage Docker cache
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create a non-root user for security
RUN useradd -m appuser
USER appuser

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port defined in application.properties
EXPOSE 8083

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
