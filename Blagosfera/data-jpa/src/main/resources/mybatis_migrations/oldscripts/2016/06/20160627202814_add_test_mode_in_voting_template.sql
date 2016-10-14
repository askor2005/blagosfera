-- // add_test_mode_in_voting_template
-- Migration SQL that makes the change goes here.

 ALTER TABLE public.batch_voting_templates
   ADD COLUMN test_mode boolean;

-- //@UNDO
-- SQL to undo the change goes here.


