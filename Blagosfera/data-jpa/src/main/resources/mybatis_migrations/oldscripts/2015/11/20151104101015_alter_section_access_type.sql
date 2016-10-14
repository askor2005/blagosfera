-- // alter section access type
-- Migration SQL that makes the change goes here.

ALTER TABLE sections ADD COLUMN access_type integer;

-- //@UNDO
-- SQL to undo the change goes here.


