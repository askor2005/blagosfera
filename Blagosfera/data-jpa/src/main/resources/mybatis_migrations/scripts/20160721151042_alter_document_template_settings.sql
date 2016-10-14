-- // alter_document_template_settings
-- Migration SQL that makes the change goes here.

ALTER TABLE public.document_template_participant_settings ADD COLUMN participant_source_name character varying(10000);

-- //@UNDO
-- SQL to undo the change goes here.


