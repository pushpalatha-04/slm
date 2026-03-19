# Use Maven + Java to build the project
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy all files
COPY . .

# Build jar file
RUN mvn clean package -DskipTests

# Use smaller runtime image
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]