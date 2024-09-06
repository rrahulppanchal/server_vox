FROM openjdk:21-jdk
ARG JAR_FILE=target/employee_management-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]