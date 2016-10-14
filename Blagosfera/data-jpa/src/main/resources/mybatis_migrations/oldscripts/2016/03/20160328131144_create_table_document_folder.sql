-- // create table document_folder
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE document_folder_id INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

CREATE TABLE document_folder
(
  id               BIGINT NOT NULL,
  description      TEXT,
  name             TEXT   NOT NULL,
  parent_folder_id BIGINT,
  CONSTRAINT document_folder_pkey PRIMARY KEY (id),
  CONSTRAINT fk_document_folder_document_folder FOREIGN KEY (parent_folder_id)
  REFERENCES document_folder (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

ALTER TABLE flowofdocument ADD COLUMN folder_id BIGINT;

ALTER TABLE flowofdocument ADD CONSTRAINT fk_flowofdocument_document_folder FOREIGN KEY (folder_id)
REFERENCES document_folder (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

-- //@UNDO
-- SQL to undo the change goes here.


