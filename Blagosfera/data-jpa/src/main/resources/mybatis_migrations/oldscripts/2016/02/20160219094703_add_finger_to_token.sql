-- // add finger to token
-- Migration SQL that makes the change goes here.

ALTER TABLE finger_tokens ADD COLUMN finger INT NOT NULL DEFAULT 0;

-- //@UNDO
-- SQL to undo the change goes here.


