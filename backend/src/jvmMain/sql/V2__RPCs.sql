SET ROLE bugzino;

-- Define the RPCs and grant access to them to normal users.

-- Signup. Only the guest account can do this.

GRANT EXECUTE ON FUNCTION start_user_registration(varchar, varchar, varchar) TO guest;
GRANT EXECUTE ON FUNCTION complete_user_registration(varchar, varchar) TO guest;

-- RPCs from this point on are automatically granted to normal users.

ALTER DEFAULT PRIVILEGES FOR ROLE bugzino IN SCHEMA public GRANT EXECUTE ON ROUTINES TO normal_user;

CREATE OR REPLACE PROCEDURE add_ticket(IN title TEXT, IN description TEXT)
    LANGUAGE plpgsql SECURITY DEFINER SET search_path = public, pg_temp
AS $$
BEGIN
    INSERT INTO t_ticket (title, description, assignee)
    VALUES (add_ticket.title, add_ticket.description, session_user);
END;
$$;

CREATE OR REPLACE PROCEDURE edit_ticket(IN id INTEGER, IN title TEXT, IN description TEXT, IN assignee TEXT)
    LANGUAGE plpgsql SECURITY DEFINER SET search_path = public, pg_temp
AS $$
BEGIN
    UPDATE t_ticket
    SET title = edit_ticket.title, description = edit_ticket.description, assignee = edit_ticket.assignee
    WHERE t_ticket.id = edit_ticket.id;
END;
$$;
