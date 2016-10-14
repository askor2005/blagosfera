-- // create eco advisor tables
-- Migration SQL that makes the change goes here.

ALTER TABLE cashbox_products RENAME COLUMN taxes TO vat;


CREATE SEQUENCE eco_advisor_parameters_id
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

CREATE TABLE eco_advisor_parameters
(
  id                                     BIGINT         NOT NULL,
  profit_allocation_shop_account         NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
  profit_allocation_shop_owner_account   NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
  profit_allocation_shop_owner_sharebook NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
  profit_allocation_shop_sharebook       NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
  profit_in_percents                     NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
  tax_on_profit                          NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
  community_id                           BIGINT         NOT NULL,
  CONSTRAINT eco_advisor_parameters_pkey PRIMARY KEY (id)
)
WITH (
OIDS =FALSE
);


CREATE SEQUENCE eco_advisor_products_id
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

CREATE TABLE eco_advisor_products
(
  id                        BIGINT                 NOT NULL,
  code                      CHARACTER VARYING(255) NOT NULL,
  count                     NUMERIC(19, 2)         NOT NULL DEFAULT 0.00,
  final_currency            CHARACTER VARYING(255) NOT NULL,
  final_price               NUMERIC(19, 2)         NOT NULL DEFAULT 0.00,
  margin                    NUMERIC(19, 2)         NOT NULL DEFAULT 0.00,
  name                      CHARACTER VARYING(255) NOT NULL,
  unit_of_measure           CHARACTER VARYING(255) NOT NULL,
  vat                       NUMERIC(19, 2)         NOT NULL DEFAULT 0.00,
  wholesale_currency        CHARACTER VARYING(255) NOT NULL,
  wholesale_price           NUMERIC(19, 2)         NOT NULL DEFAULT 0.00,
  eco_advisor_parameters_id BIGINT                 NOT NULL,
  CONSTRAINT eco_advisor_products_pkey PRIMARY KEY (id),
  CONSTRAINT fk_d9a4q18lxcs77pk8th59378uc FOREIGN KEY (eco_advisor_parameters_id)
  REFERENCES eco_advisor_parameters (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS =FALSE
);


INSERT INTO eco_advisor_parameters (id, profit_allocation_shop_account, profit_allocation_shop_owner_account, profit_allocation_shop_owner_sharebook, profit_allocation_shop_sharebook, profit_in_percents, tax_on_profit, community_id)
VALUES (0, 0, 0, 0, 100, 50, 50, 0);

-- //@UNDO
-- SQL to undo the change goes here.


