# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

COPY pom.xml .

# تنزيل dependencies بدون build
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Healthcheck محسّن مع start-period أطول
HEALTHCHECK --interval=30s --timeout=5s --start-period=150s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]



#FROM maven:3.9-eclipse-temurin-17-alpine AS build
#
#WORKDIR /app
#
#COPY pom.xml .
#
#RUN mvn dependency:go-offline -B
#
#COPY src ./src
#
#RUN mvn clean package -DskipTests
#
#FROM eclipse-temurin:17-jre-alpine
#
#WORKDIR /app
#
#COPY --from=build /app/target/*.jar app.jar
#
#EXPOSE 8080
#
#HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
#  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1
#
#ENTRYPOINT ["java", "-jar", "app.jar"]