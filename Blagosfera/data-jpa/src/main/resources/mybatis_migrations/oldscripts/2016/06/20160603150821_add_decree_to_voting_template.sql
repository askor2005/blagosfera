-- // add_decree_to_voting_template
-- Migration SQL that makes the change goes here.

ALTER TABLE public.voting_templates ADD COLUMN success_decree character varying(10000);
ALTER TABLE public.voting_templates ADD COLUMN fail_decree character varying(10000);

-- //@UNDO
-- SQL to undo the change goes here.


