-- // create execution flow logging tables
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE execution_flow_id INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE execution_flow_arg_id INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE execution_flow_result_id INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

CREATE TABLE execution_flow
(
  id                 BIGINT                      NOT NULL,
  date               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  duration           BIGINT,
  exception_thrown   BOOLEAN                     NOT NULL,
  parent_id          BIGINT,
  request_id         TEXT,
  session_id         TEXT,
  target_class_name  TEXT                        NOT NULL,
  target_method_name TEXT                        NOT NULL,
  thread_name        TEXT                        NOT NULL,
  username           TEXT,
  CONSTRAINT execution_flow_pkey PRIMARY KEY (id)
)
WITH (
OIDS =FALSE
);

CREATE TABLE execution_flow_arg
(
  id                BIGINT NOT NULL,
  value             TEXT,
  execution_flow_id BIGINT NOT NULL,
  CONSTRAINT execution_flow_arg_pkey PRIMARY KEY (id),
  CONSTRAINT fk_execution_flow_arg_execution_flow FOREIGN KEY (execution_flow_id)
  REFERENCES execution_flow (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS =FALSE
);

CREATE TABLE execution_flow_result
(
  id                BIGINT NOT NULL,
  value             TEXT,
  execution_flow_id BIGINT NOT NULL,
  CONSTRAINT execution_flow_result_pkey PRIMARY KEY (id),
  CONSTRAINT fk_execution_flow_result_execution_flow FOREIGN KEY (execution_flow_id)
  REFERENCES execution_flow (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS =FALSE
);

-- //@UNDO
-- SQL to undo the change goes here.


