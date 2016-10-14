-- // create_association_form_search_type
-- Migration SQL that makes the change goes here.

ALTER TABLE documents_types_participants ADD COLUMN association_form_search_type integer;

-- //@UNDO
-- SQL to undo the change goes here.


