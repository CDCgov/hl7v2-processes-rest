# Step 1: Build the application using Maven
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Set log level
ENV LOG_LEVEL=INFO

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies COPY pom.xml .
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the project files
COPY src ./src

# Package the application
RUN mvn clean package

# Step 2: Create the final image with the built application

# Use a lightweight JDK runtime image
FROM amazoncorretto:17-alpine

# Set the working directory
WORKDIR /app
#RUN ls -la /app

# Copy the built JAR file from the previous stage
 COPY --from=build /app/target/hl7v2-redactor-*.*.*.jar application.jar

# Expose the application port
EXPOSE 8080

# Set the entry point to run the JAR file
 ENTRYPOINT ["java", "-jar", "application.jar"]
