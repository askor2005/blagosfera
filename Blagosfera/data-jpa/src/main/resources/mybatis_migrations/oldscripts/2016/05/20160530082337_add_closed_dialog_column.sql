-- // add_closed_dialog_column
-- Migration SQL that makes the change goes here.

ALTER TABLE dialogs ADD COLUMN closed boolean;

-- //@UNDO
-- SQL to undo the change goes here.


