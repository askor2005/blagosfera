-- // add column to sharers
-- Migration SQL that makes the change goes here.

ALTER TABLE sharers ADD COLUMN avatar_photo_src character varying(100);

-- //@UNDO
-- SQL to undo the change goes here.


