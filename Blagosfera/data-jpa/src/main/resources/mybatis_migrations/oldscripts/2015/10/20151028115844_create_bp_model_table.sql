-- // Create bp model table
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE seq_bp_model START 1;
CREATE TABLE bp_model
(
  id bigint NOT NULL,
  data TEXT NOT NULL,
  CONSTRAINT bp_model_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

-- //@UNDO
-- SQL to undo the change goes here.

DROP TABLE bp_model CASCADE;
DROP SEQUENCE seq_bp_model;


