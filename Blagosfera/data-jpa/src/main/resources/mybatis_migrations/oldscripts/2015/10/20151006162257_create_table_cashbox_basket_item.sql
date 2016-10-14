-- // create table cashbox_basket_item
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE cashbox_basket_item_id
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

CREATE TABLE cashbox_basket_item
(
  id          BIGINT                 NOT NULL,
  name        CHARACTER VARYING(255) NOT NULL,
  code        CHARACTER VARYING(255) NOT NULL,
  count       NUMERIC(19, 2) DEFAULT 0.00,
  exchange_id BIGINT                 NOT NULL,
  CONSTRAINT cashbox_basket_item_pkey PRIMARY KEY (id),
  CONSTRAINT fk_cashbox_basket_item_cashbox_exchange_log FOREIGN KEY (exchange_id)
  REFERENCES cashbox_exchange_log (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS =FALSE
);

-- //@UNDO
-- SQL to undo the change goes here.


