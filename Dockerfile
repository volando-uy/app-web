FROM openjdk:17-alpine AS build

WORKDIR /app

# Install Maven and Git
RUN apk add --no-cache maven

# Copy the project files
COPY pom.xml .
COPY src ./src
COPY lib ./lib

# Move the VolandoUY jar to the lib folder
RUN mv /app/app-central/VolandoUY/target/VolandoUY-1.0-SNAPSHOT.jar /app/lib/VolandoUY-1.0-SNAPSHOT.jar

# Package the application
RUN mvn clean package dependency:resolve -Dmaven.test.skip=true


FROM amazoncorretto:17

WORKDIR /app

ENV ENVIRONMENT="PROD"

COPY tomcat ./tomcat/
COPY catalina-wrapper.sh .
RUN chmod +x /app/catalina-wrapper.sh

COPY --from=build /app/target/app-web-jsp.war ./tomcat/webapps/

CMD ["/app/catalina-wrapper.sh"]