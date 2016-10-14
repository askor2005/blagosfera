-- // alter invites add invites count
-- Migration SQL that makes the change goes here.

ALTER TABLE invites ADD COLUMN invites_count integer;
UPDATE invites SET invites_count=1;

-- //@UNDO
-- SQL to undo the change goes here.


