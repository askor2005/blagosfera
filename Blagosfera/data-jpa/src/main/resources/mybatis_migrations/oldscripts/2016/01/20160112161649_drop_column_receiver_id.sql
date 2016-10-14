-- // drop column receiver_id
-- Migration SQL that makes the change goes here.

ALTER TABLE eco_advisor_bonus_allocation DROP COLUMN receiver_id;

-- //@UNDO
-- SQL to undo the change goes here.


