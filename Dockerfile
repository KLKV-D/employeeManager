FROM openjdk:17-alpine
ADD /target/employeeManager-0.0.1-SNAPSHOT.jar employee_manager_backend.jar
ENTRYPOINT ["java", "-jar", "employee_manager_backend.jar"]