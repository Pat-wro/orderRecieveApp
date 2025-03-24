FROM openjdk:22-slim
WORKDIR /app
COPY ./api_gateway/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]