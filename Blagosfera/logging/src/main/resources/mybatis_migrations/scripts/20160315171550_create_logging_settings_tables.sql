-- // create logging settings tables
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE logging_settings_id INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

CREATE TABLE logging_settings
(
  id          BIGINT NOT NULL,
  audit_level TEXT   NOT NULL,
  CONSTRAINT logging_settings_pkey PRIMARY KEY (id)
)
WITH (
OIDS =FALSE
);

CREATE TABLE execution_target_whitelist
(
  logging_settings_id BIGINT NOT NULL,
  whitelist           TEXT,
  CONSTRAINT fk_execution_target_whitelist_logging_settings FOREIGN KEY (logging_settings_id)
  REFERENCES logging_settings (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS =FALSE
);

CREATE TABLE execution_target_blacklist
(
  logging_settings_id BIGINT NOT NULL,
  blacklist           TEXT,
  CONSTRAINT fk_execution_target_blacklist_logging_settings FOREIGN KEY (logging_settings_id)
  REFERENCES logging_settings (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS =FALSE
);

-- //@UNDO
-- SQL to undo the change goes here.


