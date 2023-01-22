FROM openjdk:17-alpine
WORKDIR /microservices

COPY /build/libs/microservices-1.0-plain.jar build/
WORKDIR /microservices/build
EXPOSE 8000
ENTRYPOINT java -jar microservices-1.0-plain.jar