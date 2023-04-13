#!/usr/bin/env bash

SERVER=hq.hydraulic.software
SERVER_PATH=/var/lib/postgresql
REFRESH_SCRIPT_NAME=refresh-bugzino-jar.sql
EXTRA_PSQL_ARGS="-p 5433"

./gradlew :backend:uberJar
echo "select sqlj.replace_jar('file:$SERVER_PATH/backend-uber.jar', 'bugzino', true);" >/tmp/$REFRESH_SCRIPT_NAME
scp /tmp/$REFRESH_SCRIPT_NAME backend/build/libs/backend-uber.jar backend/bugzino.policy root@$SERVER:$SERVER_PATH
ssh $SERVER sudo -u postgres psql --dbname=bugzino "$EXTRA_PSQL_ARGS" -f $SERVER_PATH/$REFRESH_SCRIPT_NAME
