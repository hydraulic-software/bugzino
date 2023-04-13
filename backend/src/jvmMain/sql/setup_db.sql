-- This should be run as a superuser when connected to a fresh database 'bugzino'.

SET pljava.libjvm_location TO :'jvm_path';

-- For macOS:
-- SET pljava.libjvm_location TO '/Users/mike/Library/Java/JavaVirtualMachines/jdk-20.jdk/Contents/Home/lib/server/libjvm.dylib';

SET pljava.policy_urls TO '"file:${org.postgresql.sysconfdir}/pljava.policy","file:/var/lib/postgresql/bugzino.policy","="';
-- SET pljava.policy_urls TO '"file:${org.postgresql.sysconfdir}/pljava.policy","file:/Users/mike/Hydraulic/compose-db/ticketing/backend/bugzino.policy","="';
SET pljava.vmoptions TO '-Djava.security.manager=allow';
ALTER DATABASE bugzino SET pljava.vmoptions FROM CURRENT;
ALTER DATABASE bugzino SET pljava.libjvm_location FROM CURRENT;
ALTER DATABASE bugzino SET pljava.policy_urls FROM CURRENT;
CREATE EXTENSION pljava;

-- Lock down the database and prevent enumeration of users. We will grant access
-- back on a per user/role level.
--
-- Removing access to _all_ system tables isn't possible, but we could probably
-- remove more of them here than we do.
REVOKE ALL ON SCHEMA public FROM PUBLIC ;
REVOKE ALL ON DATABASE bugzino FROM PUBLIC ;
REVOKE SELECT ON pg_catalog.pg_user FROM PUBLIC ;
REVOKE SELECT ON pg_catalog.pg_auth_members FROM PUBLIC ;

-- The role that will own all the data and be able to create new accounts.
-- It should not have a password as we'll never log in as this user.
CREATE ROLE bugzino BYPASSRLS CREATEROLE NOLOGIN ;
GRANT ALL ON DATABASE bugzino TO bugzino;
GRANT SELECT ON TABLE pg_authid TO bugzino
;
GRANT ALL ON SCHEMA public TO bugzino;
GRANT ALL ON SCHEMA sqlj TO bugzino;

-- Create the group for end user accounts. Should have very few privs.
-- Each user will have their own db role in this role.
CREATE ROLE normal_user CONNECTION LIMIT 5 NOLOGIN NOINHERIT;
GRANT CONNECT ON DATABASE bugzino TO normal_user;
GRANT USAGE ON SCHEMA public TO normal_user;

-- create the guest role for anonymous access
CREATE USER guest WITH PASSWORD 'guest';
GRANT CONNECT ON DATABASE bugzino TO guest;
GRANT USAGE ON SCHEMA public TO guest;   -- so it can see the user registration functions.
GRANT USAGE ON SCHEMA sqlj TO guest;   -- so it can see the user registration functions.

-- A marker group so we can delete them all later (for testing/dev purposes).
-- Should have no privileges.
CREATE ROLE bugzino_app_role NOINHERIT;
GRANT bugzino_app_role TO guest;
GRANT bugzino_app_role TO normal_user;
GRANT bugzino_app_role TO bugzino;
