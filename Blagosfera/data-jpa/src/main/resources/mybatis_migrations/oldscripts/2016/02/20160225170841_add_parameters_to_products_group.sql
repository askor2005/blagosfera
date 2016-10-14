-- // add parameters to products group
-- Migration SQL that makes the change goes here.

ALTER TABLE eco_advisor_products_groups ADD COLUMN general_running_costs NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE eco_advisor_products_groups ADD COLUMN wage NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE eco_advisor_products_groups ADD COLUMN vat NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE eco_advisor_products_groups ADD COLUMN tax_on_profits NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE eco_advisor_products_groups ADD COLUMN income_tax NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE eco_advisor_products_groups ADD COLUMN proprietorship_interest NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE eco_advisor_products_groups ADD COLUMN tax_on_dividends NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE eco_advisor_products_groups ADD COLUMN company_profit NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE eco_advisor_products_groups ADD COLUMN margin NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE eco_advisor_products_groups ADD COLUMN share_value NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE eco_advisor_products_groups ADD COLUMN department_part NUMERIC(19, 2) NOT NULL DEFAULT 0.00;

-- //@UNDO
-- SQL to undo the change goes here.


