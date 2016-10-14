-- // create table user_certification_sessions
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE user_certification_sessions_id
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

CREATE TABLE user_certification_sessions
(
  id             BIGINT                      NOT NULL,
  registrator_id BIGINT                      NOT NULL,
  user_id        BIGINT                      NOT NULL,
  start_date     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  end_date       TIMESTAMP WITHOUT TIME ZONE,
  session_id     TEXT                        NOT NULL,
  success        BOOLEAN                     NOT NULL,
  CONSTRAINT user_certification_sessions_pkey PRIMARY KEY (id)
) WITH (
OIDS = FALSE
);

-- //@UNDO
-- SQL to undo the change goes here.


