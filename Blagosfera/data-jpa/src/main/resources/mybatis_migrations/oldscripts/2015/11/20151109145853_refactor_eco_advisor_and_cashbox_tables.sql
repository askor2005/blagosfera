-- // refactor eco advisor and cashbox tables
-- Migration SQL that makes the change goes here.

ALTER TABLE eco_advisor_products ADD COLUMN created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now();
ALTER TABLE eco_advisor_products ADD COLUMN updated_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now();

ALTER TABLE cashbox_products DROP COLUMN name;
ALTER TABLE cashbox_products DROP COLUMN count;
ALTER TABLE cashbox_products DROP COLUMN unit_of_measure;
ALTER TABLE cashbox_products DROP COLUMN wholesale_price;
ALTER TABLE cashbox_products DROP COLUMN wholesale_currency;
ALTER TABLE cashbox_products DROP COLUMN final_price;
ALTER TABLE cashbox_products DROP COLUMN final_currency;
ALTER TABLE cashbox_products DROP COLUMN vat;

-- //@UNDO
-- SQL to undo the change goes here.


