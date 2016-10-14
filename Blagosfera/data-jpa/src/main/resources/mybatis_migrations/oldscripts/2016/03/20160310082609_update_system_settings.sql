-- // update system settings
-- Migration SQL that makes the change goes here.

ALTER TABLE system_settings ALTER COLUMN "key" TYPE text;
ALTER TABLE system_settings ALTER COLUMN "val" TYPE text;
ALTER TABLE system_settings ALTER COLUMN "description" TYPE text;

-- //@UNDO
-- SQL to undo the change goes here.


