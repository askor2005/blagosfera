-- // alter_document_need_eds
-- Migration SQL that makes the change goes here.

ALTER TABLE public.flowofdocument
   ADD COLUMN need_sign_by_eds boolean;

-- //@UNDO
-- SQL to undo the change goes here.


