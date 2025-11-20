# Stage 1: Build with Maven
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
# Use Maven to build the project and create the jar
RUN mvn -B -DskipTests package

# Stage 2: Run the generated JAR
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copy the jar built in the previous stage; adjust the jar name if different
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
