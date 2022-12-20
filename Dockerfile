FROM openjdk:17-alpine as build
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src

RUN ./mvnw package -DskipTests

FROM openjdk:17-jre-alpine
COPY --from=build /target/employeeManager-0.0.1-SNAPSHOT.jar employee_manager_backend.jar
ENTRYPOINT ["java", "-jar", "employee_manager_backend.jar"]