-- // add multiple_winners flag
-- Migration SQL that makes the change goes here.

ALTER TABLE voting_templates
  ADD COLUMN multiple_winners BOOLEAN NOT NULL DEFAULT FALSE;

-- //@UNDO
-- SQL to undo the change goes here.


