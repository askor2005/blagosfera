-- // create table eco_advisor_bonus_allocation
-- Migration SQL that makes the change goes here.



CREATE SEQUENCE eco_advisor_bonus_allocation_id
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

CREATE TABLE eco_advisor_bonus_allocation
(
  id                        BIGINT                 NOT NULL,
  eco_advisor_parameters_id BIGINT                 NOT NULL,
  allocation_percent        NUMERIC(19, 2)         NOT NULL DEFAULT 0.00,
  target_type               CHARACTER VARYING(255) NOT NULL,
  receiver_id               BIGINT                 NOT NULL,
  CONSTRAINT eco_advisor_bonus_allocation_pkey PRIMARY KEY (id),
  CONSTRAINT fk_eco_advisor_bonus_allocation_eco_advisor_parameters FOREIGN KEY (eco_advisor_parameters_id)
  REFERENCES eco_advisor_parameters (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS = FALSE
);


-- //@UNDO
-- SQL to undo the change goes here.


