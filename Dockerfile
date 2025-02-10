FROM maven:latest AS build
WORKDIR /app

COPY assesment/pom.xml ./

COPY assesment/src ./src/

RUN mvn clean package

FROM openjdk:21

WORKDIR /app

COPY --from=build /app/target/assesment-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
