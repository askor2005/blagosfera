-- // alter_voting_template_table
-- Migration SQL that makes the change goes here.

ALTER TABLE public.voting_templates
   ADD COLUMN percent_for_win integer;

-- //@UNDO
-- SQL to undo the change goes here.


