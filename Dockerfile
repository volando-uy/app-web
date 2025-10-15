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

# UNCOMMENT THESE LINE TO USE IN LOCAL
# ENV PORT="8000"
# EXPOSE $PORT

# COMMENT THIS LINE TO USE IN LOCAL
ENV ENVIRONMENT="PROD"

COPY tomcat ./tomcat/
COPY catalina-wrapper.sh ./catalina-wrapper.sh
RUN chmod +x /app/catalina-wrapper.sh

COPY --from=build /app/target/app-web-jsp.war ./tomcat/webapps/

# UNCOMMENT THIS LINE TO USE IN LOCAL
# CMD ["/app/catalina-wrapper.sh"]

# HOW TO USE THIS DOCKERFILE IN LOCAL:
# docker build . -t volandouy:latest
# docker run -p 8000:8000 volandouy:latest
# (8000 is PORT env-var)
