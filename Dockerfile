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

COPY catalina-wrapper.sh .

RUN mvn clean package dependency:resolve


FROM tomcat:11.0.0-jdk17

COPY --from=build /app/target/app-web-jsp.war /usr/local/tomcat/webapps/

COPY --from=build /app/catalina-wrapper.sh /usr/local/tomcat/bin/

RUN chmod +x /usr/local/tomcat/bin/catalina-wrapper.sh

CMD ["catalina-wrapper.sh"]