-- // create table eco_advisor_system_parameters
-- Migration SQL that makes the change goes here.



CREATE SEQUENCE eco_advisor_system_parameters_id
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

CREATE TABLE eco_advisor_system_parameters
(
  id                      BIGINT NOT NULL,
  system_bonus_account_id BIGINT NOT NULL,
  CONSTRAINT eco_advisor_system_parameters_pkey PRIMARY KEY (id)
)
WITH (
OIDS = FALSE
);

INSERT INTO eco_advisor_system_parameters (id, system_bonus_account_id) VALUES (0, 0);


-- //@UNDO
-- SQL to undo the change goes here.


