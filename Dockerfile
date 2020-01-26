FROM openjdk:8-jdk-alpine

ARG JAR_FILE=target/find-links.jar

WORKDIR /data/DOCKER

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","app.jar"]
