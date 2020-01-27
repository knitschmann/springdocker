FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/publictransport-1.0.1.jar
WORKDIR /data/DOCKER
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
