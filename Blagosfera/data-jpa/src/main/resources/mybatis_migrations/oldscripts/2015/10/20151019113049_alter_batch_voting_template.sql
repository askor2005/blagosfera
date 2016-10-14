-- // alter_batch_voting_template
-- Migration SQL that makes the change goes here.

ALTER TABLE batch_voting_templates DROP COLUMN end_date;
ALTER TABLE batch_voting_templates DROP COLUMN voters_registration_end_date;
ALTER TABLE batch_voting_templates ADD COLUMN batch_voting_hours_count integer;
ALTER TABLE batch_voting_templates ADD COLUMN registration_hours_count integer;

-- //@UNDO
-- SQL to undo the change goes here.


