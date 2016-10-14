-- // add_position_doc_templates
-- Migration SQL that makes the change goes here.

ALTER TABLE documents_templates ADD COLUMN "position" integer;
ALTER TABLE documents_templates ALTER COLUMN "position" SET DEFAULT 0;

-- //@UNDO
-- SQL to undo the change goes here.


