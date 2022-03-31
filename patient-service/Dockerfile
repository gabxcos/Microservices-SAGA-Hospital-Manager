# BUILD stage
FROM maven:3-jdk-8 as builder

WORKDIR /project
COPY patient-service .
RUN mvn package

# PACKAGE stage
FROM openjdk:8-jdk-alpine
WORKDIR /app
ARG JAR_FILE=Patient-0.0.1-SNAPSHOT.jar
COPY --from=builder /project/target/${JAR_FILE} ./patient-service.jar
ENTRYPOINT ["java", "-jar", "patient-service.jar"]