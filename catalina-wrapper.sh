#!/bin/bash

sed -i -e "s/8080/$PORT/" /usr/local/tomcat/conf/server.xml

catalina.sh run