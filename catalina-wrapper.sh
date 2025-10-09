#!/bin/sh

sed -i -e "s/8080/$PORT/" /app/tomcat/conf/server.xml

chmod +x /app/tomcat/bin/catalina.sh
/app/tomcat/bin/catalina.sh run