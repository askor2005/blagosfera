-- // add taxes to products
-- Migration SQL that makes the change goes here.

ALTER TABLE cashbox_bonus_parameters DROP COLUMN tax_percent;
ALTER TABLE products ADD COLUMN taxes NUMERIC(19, 2) DEFAULT 0.00;

-- //@UNDO
-- SQL to undo the change goes here.


