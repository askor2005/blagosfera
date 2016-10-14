-- // alter_eco_advisor_parameters
-- Migration SQL that makes the change goes here.

delete from eco_advisor_bonus_allocation where eco_advisor_parameters_id in (select id from eco_advisor_parameters where community_id in (select community_id from eco_advisor_parameters group by community_id having count(community_id) > 1) );
delete from eco_advisor_parameters where community_id in (select community_id from eco_advisor_parameters group by community_id having count(community_id) > 1);

ALTER TABLE public.eco_advisor_parameters
  ADD CONSTRAINT community_id_uniq UNIQUE (community_id);

-- //@UNDO
-- SQL to undo the change goes here.


