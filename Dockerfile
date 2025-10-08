FROM openjdk:17-alpine AS build

RUN apk add --no-cache maven

WORKDIR /app

COPY pom.xml .

COPY src ./src

COPY lib ./lib

RUN mvn install:install-file -Dfile=lib/VolandoUY-1.0-SNAPSHOT.jar -DgroupId=com.gyabisito -DartifactId=VolandoUY -Dversion=1.0-SNAPSHOT -Dpackaging=jar clean package


FROM tomcat:11.0.0-jdk17

COPY --from=build /app/target/app-web-jsp.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]