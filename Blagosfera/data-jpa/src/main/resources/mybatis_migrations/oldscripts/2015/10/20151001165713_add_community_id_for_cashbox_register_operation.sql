-- // add community id for cashbox register operation
-- Migration SQL that makes the change goes here.

ALTER TABLE cashbox_register_shareholder ADD COLUMN community_id BIGINT NOT NULL;

-- //@UNDO
-- SQL to undo the change goes here.


