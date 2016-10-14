-- // add logout date
-- Migration SQL that makes the change goes here.

ALTER TABLE sharers
  ADD COLUMN logout_date TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE sharers
  DROP COLUMN online;

-- //@UNDO
-- SQL to undo the change goes here.


