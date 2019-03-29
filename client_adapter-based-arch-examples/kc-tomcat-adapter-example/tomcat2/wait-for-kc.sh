#!/bin/bash

MAX_LOOP=30
CNT_LOOP=1

while [ $CNT_LOOP -lt $MAX_LOOP ]; do

    echo "Keycloak SSL connection check for $CNT_LOOP times"
    curl -k https://sso.example.com/auth/ --silent -o /dev/null

    if [ $? -ne 0 ]; then
        CNT_LOOP=$((CNT_LOOP+1))
    else
        echo "Keycloak SSL connection OK!"
        /usr/local/tomcat/bin/catalina.sh run
    fi
    echo "waiting 10 sec ... "
    sleep 10

done

echo "Keycloak SSL connection check error"
echo "Tomcat couldn't start"


