-- // add margin_percentage
-- Migration SQL that makes the change goes here.

ALTER TABLE eco_advisor_products ADD COLUMN margin_percentage NUMERIC(19, 2) NOT NULL DEFAULT 0.00;

UPDATE eco_advisor_products SET margin_percentage = margin * 100 / wholesale_price_with_vat;

-- //@UNDO
-- SQL to undo the change goes here.


