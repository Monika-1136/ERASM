# Stage 1: Build stage using Maven and Eclipse Temurin JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the pom.xml and download project dependencies (cached step)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application JAR package
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage using a lightweight Eclipse Temurin JRE 21 alpine image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the compiled executable JAR file from the build stage
COPY --from=build /app/target/ERASM-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 (the default server port configured in Spring Boot application)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
