FROM openjdk:17-jdk-slim
WORKDIR /
COPY .prod_env /.prod_env
COPY /build/libs/*SNAPSHOT.jar /app.jar
EXPOSE 8080
CMD ["java","-Dspring.profiles.active=prod", "-jar", "/app.jar"]