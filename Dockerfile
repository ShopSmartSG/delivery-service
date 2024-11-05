# Stage 1: Build the application using Maven
FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Stage 2: Set up the runtime environment
# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Create the log directory and set proper permissions
RUN mkdir -p /var/log/delivery-service && \
    chmod -R 777 /var/log/delivery-service  # Ensure the app can write to the log directory

# Copy the project’s jar file into the container at /app
COPY --from=build /app/target/delivery-service.jar delivery-app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "delivery-app.jar"]

# to build image after building jar post any changes
# docker build -t delivery-service:latest .
# docker-compose up --build
# docker push simranarora264/delivery-service:latest
# docker file and docker-compose port should be same
# docker-compose down : shutdown the container
# till we shutdown the postgres image , db remains intact
#docker file has container port
#app.properties has
