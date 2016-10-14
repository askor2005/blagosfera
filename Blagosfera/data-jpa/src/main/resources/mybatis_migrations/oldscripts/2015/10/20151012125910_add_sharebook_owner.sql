-- // add sharebook owner
-- Migration SQL that makes the change goes here.

ALTER TABLE book_accounts ADD COLUMN owner_type CHARACTER VARYING(50) NOT NULL DEFAULT 'SHARER';
ALTER TABLE book_accounts ALTER COLUMN owner_type DROP DEFAULT;
ALTER TABLE book_accounts RENAME COLUMN sharer_id TO owner_id;

-- //@UNDO
-- SQL to undo the change goes here.


