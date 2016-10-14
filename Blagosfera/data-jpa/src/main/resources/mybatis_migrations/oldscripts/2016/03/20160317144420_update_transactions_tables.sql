-- // update transactions tables
-- Migration SQL that makes the change goes here.

ALTER TABLE transactions DROP COLUMN status;

ALTER TABLE transactions ALTER COLUMN "comment" TYPE TEXT;

-- //@UNDO
-- SQL to undo the change goes here.


