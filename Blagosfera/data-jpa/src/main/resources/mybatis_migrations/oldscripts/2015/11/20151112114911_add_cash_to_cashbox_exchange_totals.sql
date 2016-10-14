-- // add cash to cashbox_exchange_totals
-- Migration SQL that makes the change goes here.

ALTER TABLE cashbox_exchange_totals ADD COLUMN cash BOOLEAN DEFAULT FALSE;

-- //@UNDO
-- SQL to undo the change goes here.


