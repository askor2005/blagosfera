-- // create cashbox api tables
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE cashbox_operator_session_id
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

CREATE SEQUENCE cashbox_operations_log_id
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

CREATE TABLE cashbox_operator_session
(
  id bigint NOT NULL,
  active boolean NOT NULL,
  created_date timestamp without time zone NOT NULL,
  workplace_id character varying(255) NOT NULL,
  operator_id bigint,
  end_date timestamp without time zone,
  CONSTRAINT cashbox_operator_session_pkey PRIMARY KEY (id),
  CONSTRAINT fk_do7damrrq3knsrdcqi1y5ema1 FOREIGN KEY (operator_id)
  REFERENCES sharers (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);

CREATE TABLE cashbox_operations_log
(
  id bigint NOT NULL,
  created_date timestamp without time zone NOT NULL,
  exception_message text,
  exception_stacktrace text,
  operation character varying(255) NOT NULL,
  operator_ikp character varying(255),
  request_payload text,
  response_payload text,
  status character varying(255) NOT NULL,
  workplace_id character varying(255) NOT NULL,
  CONSTRAINT cashbox_operations_log_pkey PRIMARY KEY (id)
)
WITH (
OIDS=FALSE
);

-- //@UNDO
-- SQL to undo the change goes here.


