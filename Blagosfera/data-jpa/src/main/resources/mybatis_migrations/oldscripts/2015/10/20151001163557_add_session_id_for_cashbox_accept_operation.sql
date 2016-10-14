-- // add session id for cashbox accept operation
-- Migration SQL that makes the change goes here.

ALTER TABLE cashbox_register_shareholder rename COLUMN session_id TO request_session_id;
ALTER TABLE cashbox_register_shareholder ADD COLUMN accept_session_id BIGINT;

-- //@UNDO
-- SQL to undo the change goes here.


