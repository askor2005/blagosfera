-- // add ecoadvisor parameters
-- Migration SQL that makes the change goes here.

ALTER TABLE eco_advisor_parameters ADD COLUMN share_value NUMERIC(19, 2) NOT NULL DEFAULT 0.00;

-- //@UNDO
-- SQL to undo the change goes here.


