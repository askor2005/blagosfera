-- // add ecoadvisor parameters
-- Migration SQL that makes the change goes here.

ALTER TABLE eco_advisor_parameters RENAME COLUMN profit_in_percents TO general_running_costs;
ALTER TABLE eco_advisor_parameters RENAME COLUMN tax_on_profit TO tax_on_profits;

ALTER TABLE eco_advisor_parameters ADD COLUMN wage NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE eco_advisor_parameters ADD COLUMN vat NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE eco_advisor_parameters ADD COLUMN income_tax NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE eco_advisor_parameters ADD COLUMN proprietorship_interest NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE eco_advisor_parameters ADD COLUMN tax_on_dividends NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE eco_advisor_parameters ADD COLUMN company_profit NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE eco_advisor_parameters ADD COLUMN margin NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE eco_advisor_parameters ADD COLUMN department_part NUMERIC(19, 2) NOT NULL DEFAULT 0.00;

-- //@UNDO
-- SQL to undo the change goes here.


