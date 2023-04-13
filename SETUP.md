# How to deploy

To create an instance you'll need to do two things:

1. Deploy the frontend app.
2. Set up a PostgreSQL database. These instructions are tested with 14, the pl/java extension needs a patch to compile against 15.

# Frontend app

One of the big reasons people use browsers for CRUD apps is deployment. Conveyor is a tool that aims to make releasing desktop apps as easy as a web app (disclosure: [the same people](https://www.hydraulic.software) made Conveyor and this app).

Firstly, customize the deployment for your setup:

1. Make sure to update the `app.vcs-url` key in `conveyor.conf` to point at your own repo instead of ours, so you can release directly to GH Releases+Pages.
2. Set up the PostgreSQL DB by following the instructions below and update the base64 encoded certificate string in `conveyor.conf`, so the app will connect to it. 

For local testing, run `./gradlew jar` and then:

```
conveyor run
```

To do a release, update the version number in `build.gradle.kts`, rerun `./gradlew test jar`, then:

```
conveyor make copied-site
```

That will create, sign and upload packages for Windows, macOS (Intel/ARM) and Linux fully locally. It'll take a few minutes as it has to download the JVMs for each platform, optimize them for the app, wait for Apple to notarize the software etc (if signing with proper certificates).

To learn how to get and set up Conveyor, see [the docs](https://conveyor.hydraulic.dev).

# Backend (the database)

This part is more work. Here's an example of how to install and configure the database on Ubuntu 22.04 LTS "Jammy Jellyfish". You could also use a hosted PostgreSQL cloud service, but this can be done for free on e.g. cloud free tiers, Oracle Always Free etc:

The server host name is in the `$SERVER` environment variable for these instructions, and we assume there is a remote user with the same name as your local user.

## Configure the remote host

We'll install postgres and pl/java, set up connectivity and expose the server to the internet in a secure way.

```bash
ssh root@$SERVER   # or ssh user@server and sudo

# Make sure PostgreSQL is actually installed
apt install postgresql-common

# Enable the upstream PostgreSQL apt repository:
sh /usr/share/postgresql-common/pgdg/apt.postgresql.org.sh

export POSTGRES_VERSION=14   # or whichever version you want to use

# Install postgresql (or switch to the upstream version) and also 
# PL/Java for running JVM stored procs/extensions.
apt install postgresql-$POSTGRES_VERSION openjdk-19-jdk-headless postgresql-$POSTGRES_VERSION-pljava

# Allow remote password logins as long as they come via SSL. 
cat <<EOF >>/etc/postgresql/$POSTGRES_VERSION/main/pg_hba.conf
hostssl bugzino         all             0.0.0.0/0              scram-sha-256
hostssl bugzino         all             ::0/0                  scram-sha-256
EOF

# Make the DB listen on external IP addresses.
cat <<EOF >/etc/postgresql/$POSTGRES_VERSION/main/conf.d/accept-remote-connections.conf
listen_addresses = '*'
max_connections = 500
EOF

# Generate a secure random password, show it so it can be copied somewhere safe locally.
export RANDOM_PASS=$( head -n 256 /dev/urandom | shasum -a 256 )
echo $RANDOM_PASS 

# Set it on the postgres superuser.
su - postgres
psql --command="ALTER USER postgres PASSWORD '$RANDOM_PASS'"

# Restart the server (the service name may vary on your setup).
systemctl restart postgresql@14-main

# Open the PostgreSQL port to the world.
ufw allow 5432/tcp

# Grab the contents of the auto-generated self-signed SSL certificate
cat /etc/ssl/certs/ssl-cert-snakeoil.pem
```

You now have a piece of base64 encoded data on screen. Copy the whole thing to your clipboard.

## Configure the database

The rest of these instructions should be run locally, from your laptop or desktop.

1. Edit the `conveyor.conf` file:
   1. Copy the base64 encoded certificate you printed to the screen in the previous step into it, replacing the pre-existing certificate. This will ensure the frontend will securely connect to your server and your server only.
   2. Edit the `jdbcURL` to point at your `$SERVER` (or servers, if you've set up replication).
2. Try connecting to the remote database using whatever UI you prefer, or `psql --host=$SERVER --user=postgres -W` and enter the admin password (`$RANDOM_PASS` from above).
3. Run `CREATE DATABASE bugzino;` to create the new database within the postgres server.
4. Run `./gradlew backend:uberJar` to create the JAR that we'll install into the db server.
5. Copy the JAR and policy file to the server: `scp backend/build/libs/backend-uber.jar backend/bugzino.policy root@$SERVER:/var/lib/postgresql`
6. Figure out the path to your Java install, e.g. `LIBJVM_PATH=/usr/lib/jvm/java-19-openjdk-amd64/lib/server/libjvm.so`
7. Now run `psql --host=$SERVER --port=5433 --username=postgres --dbname=bugzino -v jvm_path=$LIBJVM_PATH -v policy_path=/var/lib/postgresql/bugzino.policy -f backend/src/main/sql/setup_db.sql` to set up pl/java and the database roles. You should see words like `GRANT` and `REVOKE` scroll past.
8. Test the Java install is working: Run `psql  --host=$SERVER --port=5433 --username=postgres --dbname=bugzino --command="get_java_system_property('java.version')"` and make sure you get the JVM version back.
9. Time to create the tables. Substitute host, port and the password into this command then run it: `./gradlew -PjdbcURL='jdbc:postgresql://$SERVER/bugzino?ssl=true&sslmode=allow&user=postgres&password=$RANDOM_PASS' flywayMigrate`. Flyway should now create all the tables, views and stored procedures.

To redeploy:

1. Add the necessary SQL to the `backend/src/jvmMain/sql` directory then re-run the `flywayMigrate` command from step 9 above.
2. Edit the vars at the top of the `backend/deploy.sh` script and then run it to rebuild the backend JAR, copy it up and refresh the server.

Tips for if something goes wrong:

* If the PL/Java install goes wrong you may get weird error messages. If you get an error saying "sqlj schema not empty for CREATE EXTENSION pljava" it means you have a botched install: `DROP SCHEMA sqlj CASCADE;` and try again.
* If you get "see doc: do CREATE EXTENSION PLJAVA in new session" then you need to reconnect the session with `\c` and try again.
