-- // make search_string text column
-- Migration SQL that makes the change goes here.

ALTER TABLE sharers
  ALTER COLUMN search_string TYPE TEXT;

-- //@UNDO
-- SQL to undo the change goes here.


