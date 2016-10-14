-- // add column sharer_id to cashbox_exchange_log
-- Migration SQL that makes the change goes here.

ALTER TABLE cashbox_exchange_log ADD COLUMN sharer_id BIGINT NOT NULL;

-- //@UNDO
-- SQL to undo the change goes here.


