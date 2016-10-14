-- // alter_document_table
-- Migration SQL that makes the change goes here.

ALTER TABLE public.flowofdocument
   ADD COLUMN can_unsign_document boolean DEFAULT true;

-- //@UNDO
-- SQL to undo the change goes here.


