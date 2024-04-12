FROM openjdk:21
EXPOSE 5000
ARG JAR_FILE=BE_repo-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]