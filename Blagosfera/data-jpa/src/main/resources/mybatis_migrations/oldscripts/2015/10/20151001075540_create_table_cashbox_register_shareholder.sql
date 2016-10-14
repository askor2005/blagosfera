-- // create table cashbox register shareholder
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE cashbox_register_shareholder_id
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

CREATE TABLE cashbox_register_shareholder
(
  id                  BIGINT                      NOT NULL,
  accept_document_id  BIGINT,
  accepted_date       TIMESTAMP WITHOUT TIME ZONE,
  created_date        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  request_document_id BIGINT                      NOT NULL,
  session_id          BIGINT                      NOT NULL,
  CONSTRAINT cashbox_register_shareholder_pkey PRIMARY KEY (id),
  CONSTRAINT fk_sud5947lojwd9vapianpnu77n FOREIGN KEY (session_id)
  REFERENCES cashbox_operator_session (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS =FALSE
);

-- //@UNDO
-- SQL to undo the change goes here.


