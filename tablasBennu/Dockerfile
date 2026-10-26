FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml pom.xml
COPY src src
RUN mvn -B -ntp package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/tablasBennu-0.0.1-SNAPSHOT.jar /app/aplicacion.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/aplicacion.jar"]
