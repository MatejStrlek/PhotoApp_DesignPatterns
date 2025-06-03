FROM openjdk:17-jdk-slim

LABEL authors="matejgalic"

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]