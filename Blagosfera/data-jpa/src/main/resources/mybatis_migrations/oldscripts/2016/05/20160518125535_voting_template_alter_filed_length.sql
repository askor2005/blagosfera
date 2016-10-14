-- // voting_template_alter_filed_length
-- Migration SQL that makes the change goes here.

ALTER TABLE public.voting_templates
   ALTER COLUMN subject TYPE character varying(1000);

ALTER TABLE public.voting_templates
   ALTER COLUMN description TYPE character varying(10000);

-- //@UNDO
-- SQL to undo the change goes here.


