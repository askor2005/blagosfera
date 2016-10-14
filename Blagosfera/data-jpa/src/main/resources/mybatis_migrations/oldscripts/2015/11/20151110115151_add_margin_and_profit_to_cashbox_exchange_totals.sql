-- // add margin and profit to cashbox_exchange_totals
-- Migration SQL that makes the change goes here.

ALTER TABLE cashbox_exchange_totals ADD COLUMN total_margin NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE cashbox_exchange_totals ADD COLUMN total_profit NUMERIC(19, 2) NOT NULL DEFAULT 0.00;

-- //@UNDO
-- SQL to undo the change goes here.


