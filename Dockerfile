# Use official Java image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy jar file into container
COPY target/*.jar app.jar

# Expose port (Render will override anyway)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]