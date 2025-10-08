#!/bin/bash

sed -i -e "s/8080/$PORT/" /app/tomcat/conf/server.xml

/app/tomcat/bin/catalina.sh run