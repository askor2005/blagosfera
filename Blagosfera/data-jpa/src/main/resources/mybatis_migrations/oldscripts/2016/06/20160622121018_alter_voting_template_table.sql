-- // alter_voting_template_table
-- Migration SQL that makes the change goes here.

ALTER TABLE public.voting_templates RENAME is_fail_on_contra_result  TO stop_batch_voting_on_fail_result;

-- //@UNDO
-- SQL to undo the change goes here.


