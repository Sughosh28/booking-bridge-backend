# Use a base image with OpenJDK 21
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/eventApplication-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application will run on (default Spring Boot port is 8080)
EXPOSE 8089

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
