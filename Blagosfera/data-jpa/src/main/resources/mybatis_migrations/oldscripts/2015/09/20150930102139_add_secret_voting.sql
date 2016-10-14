-- // add secret voting
-- Migration SQL that makes the change goes here.

ALTER TABLE voting ADD COLUMN secret_voting BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE voting_batch ADD COLUMN secret_voting BOOLEAN NOT NULL DEFAULT FALSE;

-- //@UNDO
-- SQL to undo the change goes here.


