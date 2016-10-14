-- // add column bonus_account_id
-- Migration SQL that makes the change goes here.

ALTER TABLE book_accounts ADD COLUMN bonus_account_id BIGINT;

-- //@UNDO
-- SQL to undo the change goes here.


