-- // add_description_batch_voting_template
-- Migration SQL that makes the change goes here.

ALTER TABLE batch_voting_templates ADD COLUMN description character varying(100000);

-- //@UNDO
-- SQL to undo the change goes here.


