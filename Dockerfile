FROM openjdk:17-alpine
LABEL maintainer="(c) Bikmetov"
WORKDIR /app
COPY libs libs/
COPY resources resources/
COPY classes classes/

ENTRYPOINT ["java", "-Dspring.profiles.active=production", "-Xmx2048m", "-cp", "/app/resources:/app/classes:/app/libs/*", "ru.ibikmetov.microservices.MicroservicesApplicationKt"]
EXPOSE 8000