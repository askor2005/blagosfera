-- // create eco_advisor_products_groups
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE eco_advisor_products_groups_id
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

CREATE TABLE eco_advisor_products_groups
(
  id                        BIGINT NOT NULL,
  name                      TEXT   NOT NULL,
  eco_advisor_parameters_id BIGINT NOT NULL,
  CONSTRAINT eco_advisor_products_groups_pkey PRIMARY KEY (id),
  CONSTRAINT fk_eco_advisor_products_groups_eco_advisor_parameters FOREIGN KEY (eco_advisor_parameters_id) REFERENCES eco_advisor_parameters (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
) WITH (
OIDS = FALSE
);

ALTER TABLE eco_advisor_products ADD COLUMN group_id BIGINT NULL;

ALTER TABLE eco_advisor_products ADD CONSTRAINT fk_eco_advisor_products_eco_advisor_products_groups FOREIGN KEY (group_id) REFERENCES eco_advisor_products_groups (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

-- //@UNDO
-- SQL to undo the change goes here.


