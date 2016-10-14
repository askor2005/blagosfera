-- // remove unit_of_measure
-- Migration SQL that makes the change goes here.

ALTER TABLE cashbox_basket_item DROP COLUMN unit_of_measure;

-- //@UNDO
-- SQL to undo the change goes here.


