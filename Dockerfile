FROM openjdk:21-rc-jdk-slim

WORKDIR /app

COPY build/libs/quarkusgrpc-*.jar app.jar

EXPOSE 50051

CMD ["java", "-jar", "app.jar"]