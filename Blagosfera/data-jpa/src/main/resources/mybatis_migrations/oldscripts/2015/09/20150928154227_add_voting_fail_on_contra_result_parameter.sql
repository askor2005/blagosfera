-- // add voting fail on contra result parameter
-- Migration SQL that makes the change goes here.

ALTER TABLE voting
ADD COLUMN fail_on_contra_result BOOLEAN NOT NULL DEFAULT FALSE;

-- //@UNDO
-- SQL to undo the change goes here.


