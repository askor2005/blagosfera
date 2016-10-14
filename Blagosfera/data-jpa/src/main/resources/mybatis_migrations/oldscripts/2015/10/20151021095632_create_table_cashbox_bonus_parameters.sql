-- // create table cashbox_bonus_parameters
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE cashbox_bonus_parameters_id
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

CREATE TABLE cashbox_bonus_parameters
(
  id           BIGINT NOT NULL,
  community_id BIGINT NOT NULL,
  tax_percent  NUMERIC(19, 2) DEFAULT 0.00,
  sharer_bonus TEXT   NOT NULL,
  shop_bonus   TEXT   NOT NULL,
  CONSTRAINT cashbox_bonus_parameters_pkey PRIMARY KEY (id)
)
WITH (
OIDS =FALSE
);

INSERT INTO cashbox_bonus_parameters (id, community_id, tax_percent, sharer_bonus, shop_bonus)
VALUES (0, 0, 20.00, 'var sharerBonus = BigDecimal.ZERO;', 'var shopBonus = totals.getTotalFinalAmount().subtract(totals.getTotalWholesaleAmount());');

-- //@UNDO
-- SQL to undo the change goes here.


