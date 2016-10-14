-- // add columns to cashbox_basket_item
-- Migration SQL that makes the change goes here.

ALTER TABLE cashbox_basket_item ADD COLUMN unit_of_measure CHARACTER VARYING(255) NOT NULL DEFAULT 'шт';
ALTER TABLE cashbox_basket_item ADD COLUMN wholesale_price_with_vat NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE cashbox_basket_item ADD COLUMN final_price_with_vat NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE cashbox_basket_item ADD COLUMN vat NUMERIC(19, 2) NOT NULL DEFAULT 0.00;

-- //@UNDO
-- SQL to undo the change goes here.


