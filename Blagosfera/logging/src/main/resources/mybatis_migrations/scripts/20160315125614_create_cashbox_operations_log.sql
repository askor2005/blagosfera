-- // create cashbox_operations_log
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE cashbox_operations_log_id
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

CREATE TABLE cashbox_operations_log
(
  id                   BIGINT                      NOT NULL,
  created_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  exception_message    TEXT,
  exception_stacktrace TEXT,
  operation            TEXT                        NOT NULL,
  operator_ikp         TEXT,
  request_payload      TEXT,
  response_payload     TEXT,
  status               TEXT                        NOT NULL,
  workplace_id         TEXT                        NOT NULL,
  CONSTRAINT cashbox_operations_log_pkey PRIMARY KEY (id)
) WITH (
OIDS = FALSE
);

-- //@UNDO
-- SQL to undo the change goes here.


