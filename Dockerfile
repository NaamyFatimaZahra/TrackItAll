FROM eclipse-temurin:17-jdk-jammy


EXPOSE 8080

WORKDIR /app
COPY target/trackitall.jar /app/trackitall.jar

ENTRYPOINT ["java", "-jar", "trackitall.jar"]