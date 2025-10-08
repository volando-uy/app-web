FROM openjdk:17-alpine AS build

RUN apk add --no-cache maven git

RUN git clone https://github.com/volando-uy/app-central

WORKDIR /app/app-central/VolandoUY

RUN mvn clean install -DskipTests

WORKDIR /app

RUN mv ./app-central/VolandoUY/target/VolandoUY-1.0-SNAPSHOT.jar ./lib/VolandoUY-1.0-SNAPSHOT.jar

COPY pom.xml .

COPY src ./src

COPY lib ./lib

RUN mvn clean package dependency:resolve


FROM ubuntu:latest

WORKDIR /app

COPY ./apache-tomcat-11.0.12 ./apache-tomcat-11.0.12

COPY --from=build /app/target/app-web-jsp.war ./apache-tomcat-11.0.12/webapps/

COPY catalina-wrapper.sh /apache-tomcat-11.0.12/bin/catalina-wrapper.sh

RUN chmod +x /usr/local/tomcat/bin/catalina-wrapper.sh

CMD ["catalina-wrapper.sh"]