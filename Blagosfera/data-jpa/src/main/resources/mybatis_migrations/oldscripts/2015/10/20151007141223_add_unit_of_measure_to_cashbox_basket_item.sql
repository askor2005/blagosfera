-- // add unit of measure to cashbox basket item
-- Migration SQL that makes the change goes here.

ALTER TABLE cashbox_basket_item ADD COLUMN unit_of_measure CHARACTER VARYING(255) NOT NULL;

-- //@UNDO
-- SQL to undo the change goes here.


