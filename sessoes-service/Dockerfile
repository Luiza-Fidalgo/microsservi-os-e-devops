# Primeira etapa: compila o projeto usando Maven.
FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn -B -pl sessoes-service -am -DskipTests package

# Segunda etapa: executa apenas o arquivo jar.
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/sessoes-service/target/sessoes-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
