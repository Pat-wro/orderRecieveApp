FROM openjdk:22-slim
WORKDIR /app
COPY ./order_api_app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]