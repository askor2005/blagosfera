-- // add_position_document_types
-- Migration SQL that makes the change goes here.

ALTER TABLE documents_types ADD COLUMN "position" integer;
ALTER TABLE documents_types ALTER COLUMN "position" SET DEFAULT 0;
update documents_types set position = 0;

-- //@UNDO
-- SQL to undo the change goes here.


