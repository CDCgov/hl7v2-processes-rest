# Use the Maven image as the base
FROM maven:latest as builder

# Set the working directory inside the container
WORKDIR /app

# Copy the project files into the container
COPY pom.xml .
COPY src ./src

# Build the project
RUN mvn clean package -DskipTests

# Use a lightweight JDK runtime image
FROM amazoncorretto:17-alpine

#RUN apt-get update && apt-get install -y
# Set the working directory
WORKDIR /app

COPY --from=builder /app/target/hl7v2-debatcher-*.*.*.jar application.jar

# Expose the application port
EXPOSE 8080

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "application.jar"]