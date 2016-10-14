-- // update system settings
-- Migration SQL that makes the change goes here.

ALTER SEQUENCE seq_system_settings RENAME TO system_settings_id;

-- //@UNDO
-- SQL to undo the change goes here.


