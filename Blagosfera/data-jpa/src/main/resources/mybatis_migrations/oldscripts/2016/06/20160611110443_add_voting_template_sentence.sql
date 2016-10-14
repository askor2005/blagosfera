-- // add_voting_template_sentence
-- Migration SQL that makes the change goes here.

ALTER TABLE public.voting_templates ADD COLUMN sentence character varying(10000);

-- //@UNDO
-- SQL to undo the change goes here.


