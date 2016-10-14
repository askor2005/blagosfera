-- // add_document_hash_code_for_signature
-- Migration SQL that makes the change goes here.

ALTER TABLE flowofdocument ADD COLUMN hash_code_for_signature character varying(500);

-- //@UNDO
-- SQL to undo the change goes here.


