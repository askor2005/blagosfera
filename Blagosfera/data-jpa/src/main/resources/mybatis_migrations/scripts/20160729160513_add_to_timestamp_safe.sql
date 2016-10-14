-- // add_to_timestamp_safe
-- Migration SQL that makes the change goes here.

CREATE OR REPLACE FUNCTION to_timestamp_safe(text,text)
RETURNS TIMESTAMP AS $$
DECLARE result TIMESTAMP DEFAULT NULL;
BEGIN
    BEGIN
        result:= to_timestamp($1,$2);
    EXCEPTION WHEN OTHERS THEN
        RETURN NULL;
    END;
RETURN result;
END;
$$ LANGUAGE plpgsql;


-- //@UNDO
-- SQL to undo the change goes here.


