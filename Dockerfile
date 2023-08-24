FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
ENV CRYPTO_PRICES_DIRECTORY=src/main/resources/prices/
COPY ./target/CryptoAdvisorAPI-0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]