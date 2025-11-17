# syntax=docker/dockerfile:1.4
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copier uniquement pom.xml pour le cache des dépendances
COPY pom.xml .

# Utiliser le cache mount pour Maven
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Build avec cache Maven
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="khaoula-benrhebra"
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]