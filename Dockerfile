FROM maven:3.8.3-openjdk-17
COPY target/*.jar employmens_backend.jar
ENTRYPOINT java -jar employmens_backend.jar
