-- // delete remember time
-- Migration SQL that makes the change goes here.

DELETE FROM system_settings
WHERE key = 'reg.session.remember_time';

-- //@UNDO
-- SQL to undo the change goes here.


