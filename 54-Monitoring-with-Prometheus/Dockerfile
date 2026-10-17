FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn --batch-mode --no-transfer-progress package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /workspace/target/54-monitoring-with-prometheus-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080
USER 10001:10001
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
