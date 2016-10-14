-- // create table cashbox exchange log
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE cashbox_exchange_log_id
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

CREATE TABLE cashbox_exchange_log
(
  id            BIGINT                      NOT NULL,
  request_id    CHARACTER VARYING(255)      NOT NULL,
  session_id    BIGINT                      NOT NULL,
  created_date  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  accepted_date TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT cashbox_exchange_log_pkey PRIMARY KEY (id),
  CONSTRAINT fk_cashbox_exchange_log_session FOREIGN KEY (session_id)
  REFERENCES cashbox_operator_session (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS =FALSE
);

-- //@UNDO
-- SQL to undo the change goes here.


