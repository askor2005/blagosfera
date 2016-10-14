-- // ad_pdf_export_arguments
-- Migration SQL that makes the change goes here.

ALTER TABLE public.documents_templates ADD COLUMN pdf_export_arguments character varying(10000);

ALTER TABLE public.flowOfDocument ADD COLUMN pdf_export_arguments character varying(10000);

-- //@UNDO
-- SQL to undo the change goes here.


