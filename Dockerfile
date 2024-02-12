#FROM eclipse-temurin:17-jdk-alpine
FROM eclipse-temurin:21.0.1_12-jre-alpine
VOLUME /tmp
ARG JAR_FILE=build/libs/cnswapweb-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]