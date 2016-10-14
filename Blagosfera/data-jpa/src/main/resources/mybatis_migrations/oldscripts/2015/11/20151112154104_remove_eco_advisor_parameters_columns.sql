-- // remove eco_advisor_parameters columns
-- Migration SQL that makes the change goes here.

ALTER TABLE eco_advisor_parameters DROP COLUMN profit_allocation_shop_account;
ALTER TABLE eco_advisor_parameters DROP COLUMN profit_allocation_shop_owner_account;
ALTER TABLE eco_advisor_parameters DROP COLUMN profit_allocation_shop_owner_sharebook;
ALTER TABLE eco_advisor_parameters DROP COLUMN profit_allocation_shop_sharebook;

-- //@UNDO
-- SQL to undo the change goes here.


