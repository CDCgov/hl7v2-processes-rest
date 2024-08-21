# Use the Maven image as the base
FROM maven:latest as builder

# Set the working directory inside the container
WORKDIR /app

# Copy the project files into the container
COPY pom.xml .
COPY src ./src

# Build the project
RUN mvn clean package

FROM openjdk:17-slim

RUN apt-get update && apt-get install -y

COPY --from=builder /target/hl7v2-debatcher-0.1.jar app.jar

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]