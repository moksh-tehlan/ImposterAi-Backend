# Use a suitable base image (e.g., OpenJDK or a slim version)
FROM openjdk:17-jdk-slim
WORKDIR /
COPY .env /.env
COPY /build/libs/*SNAPSHOT.jar /app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app.jar"]