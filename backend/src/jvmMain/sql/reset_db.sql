DROP SCHEMA public CASCADE ;
REVOKE ALL ON SCHEMA public FROM bugzino;

DO $$
DECLARE
    member RECORD;
BEGIN
    FOR member IN (
        SELECT pg_roles.rolname
        FROM pg_roles
                 JOIN pg_auth_members ON pg_roles.oid = pg_auth_members.member
        WHERE pg_auth_members.roleid = (SELECT oid FROM pg_roles WHERE rolname = 'bugzino_app_role')
    )
        LOOP
            EXECUTE format('DROP ROLE IF EXISTS %I;', member.rolname);
        END LOOP;
END;
$$;

DROP ROLE bugzino_app_role;
