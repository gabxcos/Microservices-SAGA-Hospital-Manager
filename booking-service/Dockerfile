# BUILD stage
FROM maven:3-jdk-8 as builder

WORKDIR /project
COPY booking-service .
RUN mvn package

# PACKAGE stage
FROM openjdk:8-jdk-alpine
WORKDIR /app
ARG JAR_FILE=Booking-0.0.1-SNAPSHOT.jar
COPY --from=builder /project/target/${JAR_FILE} ./booking-service.jar
ENTRYPOINT ["java", "-jar", "booking-service.jar"]