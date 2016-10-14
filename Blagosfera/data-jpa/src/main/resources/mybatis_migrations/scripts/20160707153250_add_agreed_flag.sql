-- // add agreed flag
-- Migration SQL that makes the change goes here.

ALTER TABLE user_certification_sessions
  ADD user_agreed BOOLEAN NOT NULL DEFAULT FALSE;

-- //@UNDO
-- SQL to undo the change goes here.


