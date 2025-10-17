FROM maven:3.9.11-amazoncorretto-17 AS build

WORKDIR /app

# Copy the project files
COPY pom.xml .
COPY src ./src

# Get the JAR file
RUN mkdir lib
RUN curl https://raw.githubusercontent.com/volando-uy/app-central/main/release/VolandoUY-1.0-SNAPSHOT.jar > lib/VolandoUY-1.0-SNAPSHOT.jar

# Install the JAR
RUN mvn install:install-file \
    -Dfile=lib/VolandoUY-1.0-SNAPSHOT.jar \
    -DgroupId=com.gyabisito \
    -DartifactId=VolandoUY \
    -Dversion=1.0-SNAPSHOT \
    -Dpackaging=jar

# Package the application
RUN mvn clean package dependency:resolve -Dmaven.test.skip=true


FROM amazoncorretto:17-alpine

WORKDIR /app

# Necesario para convertir CRLF -> LF
RUN apk add --no-cache dos2unix

COPY tomcat ./tomcat/
COPY catalina-wrapper.sh ./catalina-wrapper.sh
RUN chmod +x /app/catalina-wrapper.sh

# Corregimos los saltos de línea de Windows
RUN find /app/tomcat/bin -name "*.sh" -exec dos2unix {} \;

COPY --from=build /app/target/app-web-jsp.war ./tomcat/webapps/

ENV PORT="8000"
ENV ENVIRONMENT="PROD"
EXPOSE $PORT

CMD ["/app/catalina-wrapper.sh"]
