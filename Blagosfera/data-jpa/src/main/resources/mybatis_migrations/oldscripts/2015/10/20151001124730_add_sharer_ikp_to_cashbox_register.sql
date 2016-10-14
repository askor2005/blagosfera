-- // add sharer ikp to cashbox register
-- Migration SQL that makes the change goes here.

ALTER TABLE cashbox_register_shareholder ADD COLUMN sharer_ikp CHARACTER VARYING(20) NOT NULL;

-- //@UNDO
-- SQL to undo the change goes here.


