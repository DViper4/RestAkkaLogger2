FROM openjdk:12-alpine
WORKDIR /Users/odedbachenheimer/Documents/RestLogger
COPY target/RestLogger-0.0.1-SNAPSHOT.jar /demo.jar
CMD ["java", "-jar", "/demo.jar"]
