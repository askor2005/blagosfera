-- // create_letter_of_authority_attributes
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE seq_letters_of_authority_attributes
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 20
  CACHE 1;
ALTER TABLE seq_letters_of_authority_attributes
  OWNER TO kabinet;

CREATE TABLE letters_of_authority_attributes
(
  id bigint NOT NULL,
  name character varying(255) NOT NULL,
  value character varying(255) NOT NULL,
  letter_authority_id bigint NOT NULL,
  CONSTRAINT letters_of_authority_attributes_pkey PRIMARY KEY (id),
  CONSTRAINT fk_im5omlhwt25o336e3iwy8c9dj FOREIGN KEY (letter_authority_id)
      REFERENCES letters_of_authorities (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE letters_of_authority_attributes
  OWNER TO kabinet;

-- //@UNDO
-- SQL to undo the change goes here.


