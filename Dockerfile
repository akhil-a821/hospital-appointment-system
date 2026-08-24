# Stage 1: Build the Spring Boot application using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the Spring Boot application in a lightweight JRE container
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/hospital-appointment-system-2.0.0.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
