-- // add details to login log
-- Migration SQL that makes the change goes here.

ALTER TABLE login_log_entries ADD COLUMN device CHARACTER VARYING(255);
ALTER TABLE login_log_entries ADD COLUMN os CHARACTER VARYING(255);
ALTER TABLE login_log_entries ADD COLUMN browser CHARACTER VARYING(255);

-- //@UNDO
-- SQL to undo the change goes here.


