-- // update_voting_template
-- Migration SQL that makes the change goes here.

ALTER TABLE public.voting_templates ADD COLUMN min_winners_count bigint;
ALTER TABLE public.voting_templates ADD COLUMN max_winners_count bigint;

-- //@UNDO
-- SQL to undo the change goes here.


