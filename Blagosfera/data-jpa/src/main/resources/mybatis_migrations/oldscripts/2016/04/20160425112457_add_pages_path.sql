-- // add pages path
-- Migration SQL that makes the change goes here.

ALTER TABLE pages
  ADD COLUMN path TEXT;

UPDATE pages
SET path = 'welcome'
WHERE id = 76;

UPDATE pages
SET path = 'contacts'
WHERE id = 55;

UPDATE pages
SET path = 'partners'
WHERE id = 10;

-- //@UNDO
-- SQL to undo the change goes here.


