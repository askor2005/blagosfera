-- // add prices to cashbox_basket_item
-- Migration SQL that makes the change goes here.



ALTER TABLE cashbox_basket_item ADD COLUMN base_count NUMERIC(19, 2);
ALTER TABLE cashbox_basket_item ADD COLUMN wholesale_price NUMERIC(19, 2);
ALTER TABLE cashbox_basket_item ADD COLUMN wholesale_currency CHARACTER VARYING(255);
ALTER TABLE cashbox_basket_item ADD COLUMN final_price NUMERIC(19, 2);
ALTER TABLE cashbox_basket_item ADD COLUMN final_currency CHARACTER VARYING(255);
ALTER TABLE cashbox_basket_item ADD COLUMN exchange_totals_id BIGINT;

UPDATE cashbox_basket_item cbi
SET exchange_totals_id = (SELECT cet.id
                          FROM cashbox_exchange_totals cet
                          WHERE cet.exchange_id = cbi.exchange_id);

ALTER TABLE cashbox_basket_item ALTER COLUMN exchange_totals_id SET NOT NULL;

ALTER TABLE cashbox_basket_item ADD CONSTRAINT fk_cashbox_basket_item_cashbox_exchange_totals FOREIGN KEY (exchange_totals_id)
REFERENCES cashbox_exchange_totals (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE cashbox_basket_item DROP CONSTRAINT fk_cashbox_basket_item_cashbox_exchange_log;
ALTER TABLE cashbox_basket_item DROP COLUMN exchange_id;


-- //@UNDO
-- SQL to undo the change goes here.


