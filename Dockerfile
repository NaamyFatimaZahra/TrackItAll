FROM eclipse-temurin:17-jdk-jammy


EXPOSE 8080

WORKDIR /app
COPY target/trackitall.war /app/trackitall.war

ENTRYPOINT ["java", "-jar", "trackitall.war"]