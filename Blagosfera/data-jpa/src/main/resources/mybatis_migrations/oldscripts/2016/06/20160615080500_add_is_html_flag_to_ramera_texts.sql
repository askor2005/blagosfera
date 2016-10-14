-- // add is_html flag to ramera_texts
-- Migration SQL that makes the change goes here.

ALTER TABLE ramera_texts
  ADD COLUMN is_html BOOLEAN NOT NULL DEFAULT TRUE;

-- //@UNDO
-- SQL to undo the change goes here.


