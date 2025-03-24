FROM openjdk:22-slim
WORKDIR /app
COPY ./order_processing_app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]