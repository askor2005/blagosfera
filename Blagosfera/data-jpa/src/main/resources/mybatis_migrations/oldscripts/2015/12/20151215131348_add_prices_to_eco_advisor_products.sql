-- // add prices to eco_advisor_products
-- Migration SQL that makes the change goes here.

ALTER TABLE eco_advisor_products ADD COLUMN final_price_with_vat NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE eco_advisor_products ADD COLUMN wholesale_price_with_vat NUMERIC(19, 2) NOT NULL DEFAULT 0.00;

-- //@UNDO
-- SQL to undo the change goes here.


