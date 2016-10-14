-- // replace formulas with percents in cashbox_bonus_parameters
-- Migration SQL that makes the change goes here.


ALTER TABLE cashbox_bonus_parameters DROP COLUMN sharer_bonus;
ALTER TABLE cashbox_bonus_parameters DROP COLUMN shop_bonus;

ALTER TABLE cashbox_bonus_parameters ADD COLUMN sharer_bonus NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE cashbox_bonus_parameters ADD COLUMN shop_bonus NUMERIC(19, 2) NOT NULL DEFAULT 0.00;


-- //@UNDO
-- SQL to undo the change goes here.


