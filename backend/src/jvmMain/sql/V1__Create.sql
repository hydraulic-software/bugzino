DO $$
BEGIN
    IF current_database() != 'bugzino' THEN
        RAISE NOTICE 'You are not connected to the "bugzino" database.';
    END IF;
END $$;

-- Deploy the server extension. To redeploy look at the deploy.sh script
SELECT sqlj.install_jar('${backend_jar_path}', 'bugzino', true);
SELECT sqlj.set_classpath('public', 'bugzino');

-- Reallocate ownership of the newly defined functions. There's probably a better way to do this.
DO $$
    DECLARE
        function_record RECORD;
    BEGIN
        -- Get a list of all functions in the schema, along with their current owners
        FOR function_record IN
            SELECT proname, pronamespace, pg_get_userbyid(proowner) AS owner
            FROM pg_proc
            WHERE pronamespace = 'public'::regnamespace AND prokind = 'f'
            LOOP
                -- Change the owner of each function to the new owner
                EXECUTE 'ALTER FUNCTION public.' || function_record.proname ||
                        ' OWNER TO bugzino;';
            END LOOP;
    END;
$$;


-- Start creating app objects.
SET ROLE bugzino;

-- define the tickets table
CREATE TABLE t_ticket (
  id SERIAL PRIMARY KEY,
  title TEXT NOT NULL,
  description TEXT NOT NULL,
  assignee TEXT
);

-- define the ticket view
CREATE VIEW ticket AS
  SELECT id, title, description, assignee FROM t_ticket;

GRANT SELECT ON ticket TO normal_user;

-- users, internal tables

CREATE TABLE t_users (
    email TEXT PRIMARY KEY,
    name TEXT,
    db_user OID    -- Can't make a foreign key constraint on a system table.
);

CREATE TABLE t_pending_user_registrations (
    -- the email address the user chose
    email TEXT PRIMARY KEY,
    -- their name
    name TEXT,
    -- the secret code they need to receive and give back to us
    code TEXT,
    -- desired password (unhashed)
    password TEXT
);


--  spec:548d1f78e6ebad94
