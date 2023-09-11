FROM amazoncorretto:11-alpine-jdk
COPY stats-service-client/target/*.jar stats-service-client.jar
COPY stats-service-dto/target/*.jar stats-service-dto.jar
COPY stats-service-server/target/*.jar stats-service-server.jar
ENTRYPOINT ["java","-jar","/stats-service-server.jar"]