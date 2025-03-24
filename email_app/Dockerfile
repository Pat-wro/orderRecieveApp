FROM openjdk:22-slim
WORKDIR /app
COPY ./email_app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]