-- // create session_keys
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE session_keys_id
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

CREATE TABLE session_keys
(
  id          BIGINT                      NOT NULL,
  session_id  TEXT                        NOT NULL,
  start_date  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  end_date    TIMESTAMP WITHOUT TIME ZONE,
  session_key TEXT                        NOT NULL,
  CONSTRAINT session_keys_pkey PRIMARY KEY (id)
) WITH (
OIDS = FALSE
);

-- //@UNDO
-- SQL to undo the change goes here.


