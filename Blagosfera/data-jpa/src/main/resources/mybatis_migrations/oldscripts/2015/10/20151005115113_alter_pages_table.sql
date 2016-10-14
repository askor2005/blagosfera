-- // alter_pages_table
-- Migration SQL that makes the change goes here.

ALTER TABLE pages ADD COLUMN current_editor_date timestamp without time zone;
ALTER TABLE pages ADD COLUMN current_editor_id bigint;
ALTER TABLE pages
  ADD CONSTRAINT fk_q5j3m47ce5sgeyjs927c4tb2p FOREIGN KEY (current_editor_id)
      REFERENCES sharers (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- //@UNDO
-- SQL to undo the change goes here.


