FROM amazoncorretto:21
COPY target/*.jar app.jar
WORKDIR /app
ENTRYPOINT ["java","-jar","/app.jar"]