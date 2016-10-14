-- // add sharers.bcrypt_pass
-- Migration SQL that makes the change goes here.

ALTER TABLE sharers ADD COLUMN bcrypt_pass TEXT DEFAULT NULL;

-- //@UNDO
-- SQL to undo the change goes here.


