-- // create_field_files
-- Migration SQL that makes the change goes here.

-- Table: field_files

-- DROP TABLE field_files;

CREATE TABLE field_files
(
  id bigint NOT NULL,
  name character varying(1000),
  url character varying(10000),
  field_value_id bigint NOT NULL,
  CONSTRAINT field_files_pkey PRIMARY KEY (id),
  CONSTRAINT field_value_id_fk FOREIGN KEY (field_value_id)
      REFERENCES field_values (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE field_files
  OWNER TO kabinet;


-- DROP SEQUENCE seq_email_templates;

CREATE SEQUENCE seq_field_files
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE seq_field_files
  OWNER TO kabinet;

-- //@UNDO
-- SQL to undo the change goes here.


