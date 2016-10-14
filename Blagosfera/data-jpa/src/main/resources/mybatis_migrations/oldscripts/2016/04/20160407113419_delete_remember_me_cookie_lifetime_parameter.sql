-- // delete remember me cookie lifetime parameter
-- Migration SQL that makes the change goes here.

DELETE FROM system_settings
WHERE key = 'persistent.token.remeber-me.keep-alive';

-- //@UNDO
-- SQL to undo the change goes here.


